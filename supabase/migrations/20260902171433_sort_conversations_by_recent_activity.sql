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
set search_path = public
as $function$
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
  order by coalesce(last_message.created_at, conversation.created_at) desc;
$function$;
