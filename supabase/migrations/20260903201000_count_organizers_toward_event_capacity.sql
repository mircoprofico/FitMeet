begin;

-- Capacity includes the organizer. The event-row lock in join_event keeps
-- this check and the insert atomic when several users join at once.
create or replace function public.join_event(p_event_id uuid)
returns public.event_participants
language plpgsql
security definer
set search_path = public
as $function$
declare
  target_event public.events;
  participant public.event_participants;
begin
  if (select auth.uid()) is null then
    raise exception 'Authentication is required';
  end if;

  select * into target_event
  from public.events
  where id = p_event_id
  for update;

  if not found then raise exception 'Event not found'; end if;
  if target_event.cancelled_at is not null then raise exception 'Event is cancelled'; end if;
  if target_event.starts_at <= now() then raise exception 'Event has already started'; end if;
  if target_event.organizer_id = (select auth.uid()) then
    raise exception 'Organizers cannot join their own event';
  end if;

  select * into participant
  from public.event_participants
  where event_id = p_event_id and user_id = (select auth.uid());
  if found then return participant; end if;

  if (
    select count(*)
    from (
      select target_event.organizer_id as user_id
      union
      select user_id
      from public.event_participants
      where event_id = p_event_id
    ) attendees
  ) >= target_event.capacity then
    raise exception 'Event is full';
  end if;

  insert into public.event_participants (event_id, user_id)
  values (p_event_id, (select auth.uid()))
  returning * into participant;
  return participant;
end;
$function$;

revoke all on function public.join_event(uuid) from public, anon;
grant execute on function public.join_event(uuid) to authenticated;

commit;
