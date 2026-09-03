begin;

-- Preserve the duration selected during creation rather than requiring each
-- consumer to infer it from two timestamps. Existing events are backfilled.
alter table public.events
  add column duration_minutes integer;

update public.events
set duration_minutes = coalesce(
  greatest(0, floor(extract(epoch from ends_at - starts_at) / 60)::integer),
  0
)
where duration_minutes is null;

alter table public.events
  alter column duration_minutes set default 0,
  alter column duration_minutes set not null,
  add constraint events_duration_minutes_nonnegative
    check (duration_minutes >= 0);

commit;
