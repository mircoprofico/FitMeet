begin;

-- The list needs to know whether the current user is already attending,
-- otherwise the join button cannot turn into a leave button.
create or replace function public.my_activities()
returns table (
  id uuid,
  title text,
  sport_slug text,
  starts_at timestamptz,
  location_name text,
  location text,
  level text,
  capacity integer,
  participant_count integer,
  is_joined boolean,
  is_organizer boolean
)
language sql
stable
set search_path = public, extensions
as $function$
  select
    event.id,
    event.title,
    event.sport_slug,
    event.starts_at,
    event.location_name,
    st_astext(event.location::geometry),
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

commit;
