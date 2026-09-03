begin;

-- The return schema changes, so PostgreSQL requires recreating each function
-- rather than replacing it in place. Keep both RPCs aligned with Activity.
drop function public.my_activities();

create function public.my_activities()
returns table (
  id uuid,
  title text,
  sport_slug text,
  starts_at timestamptz,
  location_name text,
  location text,
  description text,
  level text,
  capacity integer,
  participant_count integer
)
language sql
stable
security invoker
set search_path = public, extensions
as $function$
  select
    event.id,
    event.title,
    event.sport_slug,
    event.starts_at,
    event.location_name,
    st_astext(event.location::geometry),
    event.description,
    event.level,
    event.capacity,
    participant_count.count
  from public.events event
  left join lateral (
    select count(*)::integer as count
    from (
      select event.organizer_id as user_id
      union
      select participant.user_id
      from public.event_participants participant
      where participant.event_id = event.id
    ) attendees
  ) participant_count on true;
$function$;

revoke execute on function public.my_activities() from public, anon;
grant execute on function public.my_activities() to authenticated;

drop function public.my_event_conversations();

create function public.my_event_conversations()
returns table (
  conversation_id uuid,
  event_id uuid,
  title text,
  sport_slug text,
  starts_at timestamptz,
  location_name text,
  location text,
  description text,
  level text,
  capacity integer,
  participant_count integer,
  is_organizer boolean,
  last_message_at timestamptz
)
language sql
stable
security invoker
set search_path = public, extensions
as $function$
  select
    conversation.id,
    event.id,
    event.title,
    event.sport_slug,
    event.starts_at,
    event.location_name,
    st_astext(event.location::geometry),
    event.description,
    event.level,
    event.capacity,
    participant_count.count,
    event.organizer_id = auth.uid(),
    last_message.created_at
  from public.event_conversations conversation
  join public.events event on event.id = conversation.event_id
  left join lateral (
    select count(*)::integer as count
    from (
      select event.organizer_id as user_id
      union
      select participant.user_id
      from public.event_participants participant
      where participant.event_id = event.id
    ) attendees
  ) participant_count on true
  left join lateral (
    select created_at
    from public.conversation_messages
    where conversation_id = conversation.id
    order by created_at desc
    limit 1
  ) last_message on true
  where public.can_access_event_conversation(event.id)
  order by coalesce(last_message.created_at, conversation.created_at) desc;
$function$;

revoke execute on function public.my_event_conversations() from public, anon;
grant execute on function public.my_event_conversations() to authenticated;

commit;
