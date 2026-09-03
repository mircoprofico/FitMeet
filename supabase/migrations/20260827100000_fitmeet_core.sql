-- FitMeet core data model: Auth-linked profiles, activities and participation.
-- All public tables have RLS enabled. Client applications use the authenticated role.

create extension if not exists postgis with schema extensions;

do $$ begin
  create type public.account_type as enum ('individual', 'club');
exception when duplicate_object then null;
end $$;

do $$ begin
  create type public.skill_level as enum ('beginner', 'intermediate', 'advanced', 'all_levels');
exception when duplicate_object then null;
end $$;

create table if not exists public.sports (
  slug text primary key check (slug ~ '^[a-z0-9-]+$'),
  name text not null unique,
  color text not null check (color ~ '^#[0-9A-Fa-f]{6}$'),
  created_at timestamptz not null default now()
);

insert into public.sports (slug, name, color) values
  ('badminton', 'Badminton', '#F59E0B'),
  ('basketball', 'Basketball', '#F97316'),
  ('cycling', 'Cycling', '#3B82F6'),
  ('football', 'Football', '#22C55E'),
  ('running', 'Running', '#EF4444'),
  ('swimming', 'Swimming', '#06B6D4'),
  ('tennis', 'Tennis', '#84CC16'),
  ('volleyball', 'Volleyball', '#8B5CF6')
on conflict (slug) do update set name = excluded.name, color = excluded.color;

create table if not exists public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  display_name text not null check (char_length(display_name) between 1 and 80),
  account_type public.account_type not null default 'individual',
  bio text check (char_length(bio) <= 500),
  city text check (char_length(city) <= 120),
  avatar_path text,
  onboarding_completed_at timestamptz,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create table if not exists public.user_sports (
  user_id uuid not null references public.profiles(id) on delete cascade,
  sport_slug text not null references public.sports(slug) on delete restrict,
  level public.skill_level not null default 'all_levels',
  created_at timestamptz not null default now(),
  primary key (user_id, sport_slug)
);

create table if not exists public.events (
  id uuid primary key default gen_random_uuid(),
  organizer_id uuid not null references public.profiles(id) on delete restrict,
  sport_slug text not null references public.sports(slug) on delete restrict,
  title text not null check (char_length(title) between 3 and 120),
  description text check (char_length(description) <= 5000),
  starts_at timestamptz not null,
  ends_at timestamptz not null,
  location_name text not null check (char_length(location_name) between 1 and 255),
  location geography(Point, 4326) not null,
  level public.skill_level not null default 'all_levels',
  capacity integer not null check (capacity between 1 and 10000),
  price_chf numeric(10, 2) not null default 0 check (price_chf >= 0),
  cancelled_at timestamptz,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  constraint event_time_range check (ends_at > starts_at)
);

create table if not exists public.event_participants (
  event_id uuid not null references public.events(id) on delete cascade,
  user_id uuid not null references public.profiles(id) on delete cascade,
  joined_at timestamptz not null default now(),
  primary key (event_id, user_id)
);

create index if not exists events_location_gix on public.events using gist (location);
create index if not exists events_starts_at_idx on public.events (starts_at);
create index if not exists events_sport_starts_at_idx on public.events (sport_slug, starts_at);
create index if not exists events_organizer_id_idx on public.events (organizer_id);
create index if not exists event_participants_user_id_idx on public.event_participants (user_id);

create or replace function public.set_updated_at()
returns trigger
language plpgsql
set search_path = public
as $$ begin new.updated_at = now(); return new; end; $$;

drop trigger if exists profiles_set_updated_at on public.profiles;
create trigger profiles_set_updated_at before update on public.profiles
for each row execute function public.set_updated_at();

drop trigger if exists events_set_updated_at on public.events;
create trigger events_set_updated_at before update on public.events
for each row execute function public.set_updated_at();

create or replace function public.handle_new_user()
returns trigger
language plpgsql
security definer
set search_path = public
as $$
begin
  insert into public.profiles (id, display_name)
  values (new.id, coalesce(nullif(trim(new.raw_user_meta_data ->> 'display_name'), ''), split_part(new.email, '@', 1), 'FitMeet user'));
  return new;
end;
$$;

drop trigger if exists on_auth_user_created on auth.users;
create trigger on_auth_user_created
after insert on auth.users
for each row execute function public.handle_new_user();

-- A transaction plus a row lock prevents two simultaneous joins from exceeding capacity.
create or replace function public.join_event(p_event_id uuid)
returns public.event_participants
language plpgsql
security definer
set search_path = public
as $$
declare
  target_event public.events;
  participant public.event_participants;
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

  if (select count(*) from public.event_participants where event_id = p_event_id) >= target_event.capacity then
    raise exception 'Event is full';
  end if;

  insert into public.event_participants (event_id, user_id)
  values (p_event_id, (select auth.uid()))
  returning * into participant;
  return participant;
end;
$$;

-- Events are visible to signed-in users. Cancelled events remain visible only to their organizer.
create or replace function public.nearby_events(
  p_latitude double precision,
  p_longitude double precision,
  p_radius_meters integer default 10000,
  p_limit integer default 50
)
returns table (
  id uuid, organizer_id uuid, sport_slug text, title text, description text,
  starts_at timestamptz, ends_at timestamptz, location_name text,
  latitude double precision, longitude double precision, level public.skill_level,
  capacity integer, price_chf numeric, participant_count bigint, distance_meters double precision
)
language sql
stable
security invoker
set search_path = public, extensions
as $$
  select e.id, e.organizer_id, e.sport_slug, e.title, e.description,
         e.starts_at, e.ends_at, e.location_name,
         extensions.st_y(e.location::extensions.geometry), extensions.st_x(e.location::extensions.geometry),
         e.level, e.capacity, e.price_chf, count(ep.user_id) as participant_count,
         extensions.st_distance(e.location, extensions.st_setsrid(extensions.st_makepoint(p_longitude, p_latitude), 4326)::extensions.geography) as distance_meters
  from public.events e
  left join public.event_participants ep on ep.event_id = e.id
  where e.cancelled_at is null
    and e.starts_at > now()
    and p_radius_meters between 1 and 50000
    and extensions.st_dwithin(e.location, extensions.st_setsrid(extensions.st_makepoint(p_longitude, p_latitude), 4326)::extensions.geography, p_radius_meters)
  group by e.id
  order by distance_meters, e.starts_at
  limit least(greatest(p_limit, 1), 100);
$$;

alter table public.sports enable row level security;
alter table public.profiles enable row level security;
alter table public.user_sports enable row level security;
alter table public.events enable row level security;
alter table public.event_participants enable row level security;

create policy "sports are readable by authenticated users" on public.sports for select to authenticated using (true);
create policy "profiles are readable by authenticated users" on public.profiles for select to authenticated using (true);
create policy "users update their own profile" on public.profiles for update to authenticated using ((select auth.uid()) = id) with check ((select auth.uid()) = id);
create policy "users manage their own sports" on public.user_sports for all to authenticated using ((select auth.uid()) = user_id) with check ((select auth.uid()) = user_id);
create policy "users see available events or their own cancelled events" on public.events for select to authenticated using (cancelled_at is null or organizer_id = (select auth.uid()));
create policy "users create their own events" on public.events for insert to authenticated with check ((select auth.uid()) = organizer_id);
create policy "organizers update their own events" on public.events for update to authenticated using ((select auth.uid()) = organizer_id) with check ((select auth.uid()) = organizer_id);
create policy "organizers delete their own events" on public.events for delete to authenticated using ((select auth.uid()) = organizer_id);
create policy "users see their own participation or participants of their events" on public.event_participants for select to authenticated using (
  user_id = (select auth.uid()) or exists (select 1 from public.events e where e.id = event_id and e.organizer_id = (select auth.uid()))
);
create policy "participants may leave non-owned events" on public.event_participants for delete to authenticated using (
  user_id = (select auth.uid()) and exists (select 1 from public.events e where e.id = event_id and e.organizer_id <> (select auth.uid()))
);

revoke all on function public.handle_new_user() from public, anon, authenticated;
revoke all on function public.set_updated_at() from public, anon, authenticated;
revoke all on function public.join_event(uuid) from public, anon;
grant execute on function public.join_event(uuid) to authenticated;
revoke all on function public.nearby_events(double precision, double precision, integer, integer) from public, anon;
grant execute on function public.nearby_events(double precision, double precision, integer, integer) to authenticated;
revoke all on function public.rls_auto_enable() from public, anon, authenticated;
