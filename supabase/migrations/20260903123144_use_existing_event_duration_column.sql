begin;

-- `duration` is the existing event field. Move the temporary backfill into
-- it, then remove the duplicate column introduced by the previous migration.
update public.events
set duration = duration_minutes
where duration is null;

alter table public.events
  drop constraint events_duration_minutes_nonnegative,
  drop column duration_minutes;

commit;
