begin;

-- Repairs three things that broke the join and leave buttons.
--
-- 1. 20260903190000 tried to add is_joined and is_organizer with
--    "create or replace", but 20260903121424 had already changed the return
--    type by adding description. PostgreSQL refuses to replace a function
--    whose result columns change ("cannot change return type of existing
--    function"), so that migration never applied: the list still receives
--    rows without is_joined, every card stays on "Rejoindre" whatever the
--    user has already joined, and "Quitter" can never appear.
--    Both columns sets are merged here, behind a drop.
--
-- 2. It also dropped description, which the detail sheet reads.
--
-- 3. participant_count was counted as the caller. The select policy on
--    event_participants only exposes your own row and the rows of events you
--    organise, so every other event reported one or two attendees instead of
--    its real crowd: "Complet" never showed and the capacity the button
--    trusted was not the capacity the server enforced. Counting is done as
--    the owner now; only the number crosses the boundary, never who.

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
  participant_count integer,
  is_joined boolean,
  is_organizer boolean
)
language sql
stable
security definer
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
    participant_count.count,
    exists (
      select 1
      from public.event_participants participant
      where participant.event_id = event.id
        and participant.user_id = (select auth.uid())
    ),
    event.organizer_id = (select auth.uid())
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

revoke all on function public.my_activities() from public, anon;
grant execute on function public.my_activities() to authenticated;

-- The conversation sheet shows the same detail screen as the list, so it
-- needs the same join state. Without is_joined its button could only ever
-- read "Rejoindre", for people who are by definition already in the event.
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
  is_joined boolean,
  is_organizer boolean,
  last_message_at timestamptz
)
language sql
stable
security definer
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
    exists (
      select 1
      from public.event_participants participant
      where participant.event_id = event.id
        and participant.user_id = (select auth.uid())
    ),
    event.organizer_id = (select auth.uid()),
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
  -- security definer bypasses RLS, so the visibility rule has to stay
  -- explicit here: this is what keeps the list to your own conversations
  where public.can_access_event_conversation(event.id)
  order by coalesce(last_message.created_at, conversation.created_at) desc;
$function$;

revoke all on function public.my_event_conversations() from public, anon;
grant execute on function public.my_event_conversations() to authenticated;

-- The counter the card shows includes the organizer, so the capacity check
-- has to count them too. Without this an event with capacity 2 and one
-- organizer reads "2/2 Complet" in the list while join_event still lets a
-- second participant in, and the button and the server disagree.
create or replace function public.join_event(p_event_id uuid)
returns public.event_participants
language plpgsql
security definer
set search_path = public
as $$
declare
  target_event public.events;
  participant public.event_participants;
  attendee_count integer;
begin
  if (select auth.uid()) is null then
    raise exception 'Authentication is required';
  end if;

  select * into target_event from public.events where id = p_event_id for update;
  if not found then raise exception 'Event not found'; end if;
  if target_event.cancelled_at is not null then raise exception 'Event is cancelled'; end if;
  if target_event.starts_at <= now() then raise exception 'Event has already started'; end if;
  if target_event.organizer_id = (select auth.uid()) then raise exception 'Organizers cannot join their own event'; end if;

  select * into participant from public.event_participants
  where event_id = p_event_id and user_id = (select auth.uid());
  if found then return participant; end if;

  select count(*)::integer into attendee_count
  from (
    select target_event.organizer_id as user_id
    union
    select existing.user_id
    from public.event_participants existing
    where existing.event_id = p_event_id
  ) attendees;

  if attendee_count >= target_event.capacity then
    raise exception 'Event is full';
  end if;

  insert into public.event_participants (event_id, user_id)
  values (p_event_id, (select auth.uid()))
  returning * into participant;
  return participant;
end;
$$;

revoke all on function public.join_event(uuid) from public, anon;
grant execute on function public.join_event(uuid) to authenticated;

commit;
