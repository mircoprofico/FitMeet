begin;

-- RLS already restricts this to a user's own participation in activities
-- they do not organize. This grant only lets the authenticated role reach
-- that policy through the Data API.
grant delete on table public.event_participants to authenticated;

commit;
