-- One group conversation exists for every event.
create table public.event_conversations (
  id uuid primary key default gen_random_uuid(),
  event_id uuid not null unique references public.events(id) on delete cascade,
  created_at timestamptz not null default now()
);

create table public.conversation_messages (
  id uuid primary key default gen_random_uuid(),
  conversation_id uuid not null references public.event_conversations(id) on delete cascade,
  sender_id uuid not null references public.profiles(id) on delete restrict,
  content text not null check (char_length(btrim(content)) between 1 and 2000),
  created_at timestamptz not null default now()
);

create index conversation_messages_conversation_created_at_idx
  on public.conversation_messages (conversation_id, created_at);

-- An event organizer and every joined participant are conversation members.
create or replace function public.can_access_event_conversation(p_event_id uuid)
returns boolean
language sql
stable
as $$
  select auth.uid() is not null and (
    exists (
      select 1
      from public.events
      where id = p_event_id and organizer_id = auth.uid()
    )
    or exists (
      select 1
      from public.event_participants
      where event_id = p_event_id and user_id = auth.uid()
    )
  );
$$;

-- Conversations are made automatically when an event is created.
create or replace function public.create_event_conversation()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
  insert into public.event_conversations (event_id)
  values (new.id)
  on conflict (event_id) do nothing;
  return new;
end;
$$;

create trigger create_event_conversation_after_insert
after insert on public.events
for each row execute function public.create_event_conversation();

-- Events that existed before this migration also receive a conversation.
insert into public.event_conversations (event_id)
select id from public.events
on conflict (event_id) do nothing;

alter table public.event_conversations enable row level security;
alter table public.conversation_messages enable row level security;

create policy "Conversation members can read conversations"
on public.event_conversations
for select to authenticated
using (public.can_access_event_conversation(event_id));

create policy "Conversation members can read messages"
on public.conversation_messages
for select to authenticated
using (
  exists (
    select 1
    from public.event_conversations
    where id = conversation_id
      and public.can_access_event_conversation(event_id)
  )
);

create policy "Conversation members can send messages"
on public.conversation_messages
for insert to authenticated
with check (
  sender_id = auth.uid()
  and exists (
    select 1
    from public.event_conversations
    where id = conversation_id
      and public.can_access_event_conversation(event_id)
  )
);

-- Lists every activity the current user organizes or joined, with its conversation.
create or replace function public.my_event_conversations()
returns table (
  conversation_id uuid,
  event_id uuid,
  title text,
  starts_at timestamptz,
  ends_at timestamptz,
  location_name text,
  is_organizer boolean,
  last_message_at timestamptz
)
language sql
stable
as $$
  select
    conversation.id,
    event.id,
    event.title,
    event.starts_at,
    event.ends_at,
    event.location_name,
    event.organizer_id = auth.uid(),
    last_message.created_at
  from public.event_conversations conversation
  join public.events event on event.id = conversation.event_id
  left join lateral (
    select created_at
    from public.conversation_messages
    where conversation_id = conversation.id
    order by created_at desc
    limit 1
  ) last_message on true
  where public.can_access_event_conversation(event.id)
  order by event.starts_at asc;
$$;

grant execute on function public.my_event_conversations() to authenticated;
