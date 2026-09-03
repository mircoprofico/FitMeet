begin;

-- The avatar stack has never had anything to show. The policy on
-- event_participants only exposes your own row, so the client knows how many
-- people attend but not who, and every circle came out blank.
--
-- This returns names for one event at a time and nothing else: no ids, no
-- emails, no way to enumerate the table. Only for an event the caller can
-- already see in their list, which is the same rule the events policy
-- applies and which is restated here because security definer bypasses it.
create or replace function public.event_attendee_names(p_event_id uuid)
returns table (display_name text)
language sql
stable
security definer
set search_path = public, extensions
as $function$
  select profile.display_name
  from public.events event
  join lateral (
    select event.organizer_id as user_id
    union
    select participant.user_id
    from public.event_participants participant
    where participant.event_id = event.id
  ) attendee on true
  join public.profiles profile on profile.id = attendee.user_id
  where event.id = p_event_id
    and (select auth.uid()) is not null
    and (event.cancelled_at is null or event.organizer_id = (select auth.uid()))
  -- the organiser first, then alphabetical: the stack cuts off after a few
  -- circles and the person running the activity is the one worth showing
  order by (attendee.user_id = event.organizer_id) desc, profile.display_name;
$function$;

revoke all on function public.event_attendee_names(uuid) from public, anon;
grant execute on function public.event_attendee_names(uuid) to authenticated;

commit;
