drop function if exists public.nearby_events(double precision, double precision, integer, integer);

create function public.nearby_events(
  p_latitude double precision,
  p_longitude double precision,
  p_radius_meters integer default 10000,
  p_limit integer default 50
)
returns table (
  id uuid,
  organizer_id uuid,
  sport_slug text,
  title text,
  description text,
  starts_at timestamptz,
  ends_at timestamptz,
  location_name text,
  latitude double precision,
  longitude double precision,
  level skill_level,
  capacity integer,
  participant_count bigint,
  distance_meters double precision
)
language sql
stable
security definer
set search_path = public, extensions
as $function$
  select
    e.id,
    e.organizer_id,
    e.sport_slug,
    e.title,
    e.description,
    e.starts_at,
    e.ends_at,
    e.location_name,
    extensions.st_y(e.location::extensions.geometry),
    extensions.st_x(e.location::extensions.geometry),
    e.level,
    e.capacity,
    count(ep.user_id) as participant_count,
    extensions.st_distance(
      e.location,
      extensions.st_setsrid(
        extensions.st_makepoint(p_longitude, p_latitude),
        4326
      )::extensions.geography
    ) as distance_meters
  from public.events e
  left join public.event_participants ep on ep.event_id = e.id
  where (select auth.uid()) is not null
    and p_latitude between -90 and 90
    and p_longitude between -180 and 180
    and p_radius_meters between 1 and 50000
    and e.cancelled_at is null
    and e.starts_at > now()
    and extensions.st_dwithin(
      e.location,
      extensions.st_setsrid(
        extensions.st_makepoint(p_longitude, p_latitude),
        4326
      )::extensions.geography,
      p_radius_meters
    )
  group by e.id
  order by distance_meters, e.starts_at
  limit least(greatest(p_limit, 1), 100);
$function$;

revoke execute on function public.nearby_events(double precision, double precision, integer, integer)
  from public, anon;

grant execute on function public.nearby_events(double precision, double precision, integer, integer)
  to authenticated;
