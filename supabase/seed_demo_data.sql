-- Development/demo data only. Safe to run repeatedly.
-- Demo accounts use password: FitMeetDemo2026!

begin;

-- These IDs are deliberately fixed so the seed remains idempotent.
with demo_users (id, email, display_name) as (
  values
    ('10000000-0000-4000-8000-000000000001'::uuid, 'alice.martin@fitmeet.example', 'Alice Martin'),
    ('10000000-0000-4000-8000-000000000002'::uuid, 'benoit.rochat@fitmeet.example', 'Benoît Rochat'),
    ('10000000-0000-4000-8000-000000000003'::uuid, 'clara.dupont@fitmeet.example', 'Clara Dupont'),
    ('10000000-0000-4000-8000-000000000004'::uuid, 'david.muller@fitmeet.example', 'David Müller'),
    ('10000000-0000-4000-8000-000000000005'::uuid, 'emma.girard@fitmeet.example', 'Emma Girard'),
    ('10000000-0000-4000-8000-000000000006'::uuid, 'fitclub.lausanne@fitmeet.example', 'FitClub Lausanne')
)
insert into auth.users (
  id, aud, role, email, encrypted_password, email_confirmed_at,
  raw_app_meta_data, raw_user_meta_data, created_at, updated_at
)
select
  id, 'authenticated', 'authenticated', email,
  '$2a$06$.nF7kdMAE9G2RjDymUnOwuD/q09Bo4ogtYEtFhAlXJ4miVq.6QsV.',
  now(), '{"provider":"email","providers":["email"]}'::jsonb,
  jsonb_build_object('display_name', display_name), now(), now()
from demo_users
on conflict (id) do update set
  email = excluded.email,
  encrypted_password = excluded.encrypted_password,
  raw_user_meta_data = excluded.raw_user_meta_data,
  updated_at = now();

with demo_users (id, email, display_name) as (
  values
    ('10000000-0000-4000-8000-000000000001'::uuid, 'alice.martin@fitmeet.example', 'Alice Martin'),
    ('10000000-0000-4000-8000-000000000002'::uuid, 'benoit.rochat@fitmeet.example', 'Benoît Rochat'),
    ('10000000-0000-4000-8000-000000000003'::uuid, 'clara.dupont@fitmeet.example', 'Clara Dupont'),
    ('10000000-0000-4000-8000-000000000004'::uuid, 'david.muller@fitmeet.example', 'David Müller'),
    ('10000000-0000-4000-8000-000000000005'::uuid, 'emma.girard@fitmeet.example', 'Emma Girard'),
    ('10000000-0000-4000-8000-000000000006'::uuid, 'fitclub.lausanne@fitmeet.example', 'FitClub Lausanne')
)
insert into auth.identities (
  provider_id, user_id, identity_data, provider, created_at, updated_at
)
select
  id::text, id,
  jsonb_build_object('sub', id::text, 'email', email, 'email_verified', true, 'phone_verified', false),
  'email', now(), now()
from demo_users
on conflict (provider_id, provider) do update set
  identity_data = excluded.identity_data,
  updated_at = now();

-- Existing profile rows are updated as well, so the script works after a re-run.
insert into public.profiles (id, display_name, account_type, bio, city, onboarding_completed_at)
values
  ('10000000-0000-4000-8000-000000000001', 'Alice Martin', 'individual', 'Running enthusiast and weekend hiker.', 'Lausanne', now()),
  ('10000000-0000-4000-8000-000000000002', 'Benoît Rochat', 'individual', 'Always up for football or cycling.', 'Renens', now()),
  ('10000000-0000-4000-8000-000000000003', 'Clara Dupont', 'individual', 'Tennis player looking for regular partners.', 'Pully', now()),
  ('10000000-0000-4000-8000-000000000004', 'David Müller', 'individual', 'Basketball, swimming and a good coffee afterwards.', 'Lausanne', now()),
  ('10000000-0000-4000-8000-000000000005', 'Emma Girard', 'individual', 'Yoga and beginner-friendly running sessions.', 'Ecublens', now()),
  ('10000000-0000-4000-8000-000000000006', 'FitClub Lausanne', 'club', 'Local club organising inclusive group activities.', 'Lausanne', now())
on conflict (id) do update set
  display_name = excluded.display_name,
  account_type = excluded.account_type,
  bio = excluded.bio,
  city = excluded.city,
  onboarding_completed_at = excluded.onboarding_completed_at;

insert into public.user_sports (user_id, sport_slug, level) values
  ('10000000-0000-4000-8000-000000000001', 'running', 'intermediate'),
  ('10000000-0000-4000-8000-000000000001', 'cycling', 'beginner'),
  ('10000000-0000-4000-8000-000000000002', 'football', 'intermediate'),
  ('10000000-0000-4000-8000-000000000002', 'cycling', 'advanced'),
  ('10000000-0000-4000-8000-000000000003', 'tennis', 'intermediate'),
  ('10000000-0000-4000-8000-000000000004', 'basketball', 'advanced'),
  ('10000000-0000-4000-8000-000000000004', 'swimming', 'intermediate'),
  ('10000000-0000-4000-8000-000000000005', 'running', 'beginner'),
  ('10000000-0000-4000-8000-000000000006', 'volleyball', 'all_levels'),
  ('10000000-0000-4000-8000-000000000006', 'badminton', 'all_levels')
on conflict (user_id, sport_slug) do update set level = excluded.level;

insert into public.events (
  id, organizer_id, sport_slug, title, description, starts_at, ends_at,
  location_name, location, level, capacity, price_chf
) values
  ('20000000-0000-4000-8000-000000000001', '10000000-0000-4000-8000-000000000001', 'running', 'Morning run by the lake', 'Relaxed 6 km run along Ouchy. Coffee afterwards for anyone interested.', now() + interval '1 day', now() + interval '1 day 1 hour', 'Ouchy Olympic Museum', extensions.st_setsrid(extensions.st_makepoint(6.6310, 46.5071), 4326)::extensions.geography, 'all_levels', 12, 0),
  ('20000000-0000-4000-8000-000000000002', '10000000-0000-4000-8000-000000000002', 'football', 'Five-a-side after work', 'Friendly five-a-side match. Bring indoor shoes and a dark or light t-shirt.', now() + interval '1 day 3 hours', now() + interval '1 day 4 hours 30 minutes', 'Centre sportif de Dorigny', extensions.st_setsrid(extensions.st_makepoint(6.5788, 46.5239), 4326)::extensions.geography, 'intermediate', 10, 5),
  ('20000000-0000-4000-8000-000000000003', '10000000-0000-4000-8000-000000000003', 'tennis', 'Saturday tennis doubles', 'Looking for two players for mixed-level doubles.', now() + interval '2 days', now() + interval '2 days 2 hours', 'Tennis Club Lausanne-Sports', extensions.st_setsrid(extensions.st_makepoint(6.6384, 46.5173), 4326)::extensions.geography, 'intermediate', 4, 8),
  ('20000000-0000-4000-8000-000000000004', '10000000-0000-4000-8000-000000000004', 'basketball', 'Open basketball session', 'Casual half-court and full-court games depending on numbers.', now() + interval '3 days', now() + interval '3 days 2 hours', 'Vidy basketball courts', extensions.st_setsrid(extensions.st_makepoint(6.6014, 46.5142), 4326)::extensions.geography, 'all_levels', 16, 0),
  ('20000000-0000-4000-8000-000000000005', '10000000-0000-4000-8000-000000000005', 'running', 'Couch to 5K group', 'A gentle run/walk session for beginners. No one gets left behind.', now() + interval '4 days', now() + interval '4 days 1 hour', 'Parc de Milan', extensions.st_setsrid(extensions.st_makepoint(6.6277, 46.5193), 4326)::extensions.geography, 'beginner', 15, 0),
  ('20000000-0000-4000-8000-000000000006', '10000000-0000-4000-8000-000000000006', 'volleyball', 'Sunday volleyball at the beach', 'Outdoor volleyball, all experience levels welcome.', now() + interval '5 days', now() + interval '5 days 2 hours', 'Plage de Vidy', extensions.st_setsrid(extensions.st_makepoint(6.6038, 46.5101), 4326)::extensions.geography, 'all_levels', 18, 0)
on conflict (id) do update set
  organizer_id = excluded.organizer_id, sport_slug = excluded.sport_slug, title = excluded.title,
  description = excluded.description, starts_at = excluded.starts_at, ends_at = excluded.ends_at,
  location_name = excluded.location_name, location = excluded.location, level = excluded.level,
  capacity = excluded.capacity, price_chf = excluded.price_chf, cancelled_at = null;

insert into public.event_participants (event_id, user_id) values
  ('20000000-0000-4000-8000-000000000001', '10000000-0000-4000-8000-000000000002'),
  ('20000000-0000-4000-8000-000000000001', '10000000-0000-4000-8000-000000000005'),
  ('20000000-0000-4000-8000-000000000002', '10000000-0000-4000-8000-000000000001'),
  ('20000000-0000-4000-8000-000000000002', '10000000-0000-4000-8000-000000000004'),
  ('20000000-0000-4000-8000-000000000003', '10000000-0000-4000-8000-000000000001'),
  ('20000000-0000-4000-8000-000000000003', '10000000-0000-4000-8000-000000000004'),
  ('20000000-0000-4000-8000-000000000004', '10000000-0000-4000-8000-000000000002'),
  ('20000000-0000-4000-8000-000000000004', '10000000-0000-4000-8000-000000000006'),
  ('20000000-0000-4000-8000-000000000005', '10000000-0000-4000-8000-000000000003'),
  ('20000000-0000-4000-8000-000000000006', '10000000-0000-4000-8000-000000000001'),
  ('20000000-0000-4000-8000-000000000006', '10000000-0000-4000-8000-000000000005')
on conflict (event_id, user_id) do nothing;

commit;
