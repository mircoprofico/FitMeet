alter table public.profiles
  add column if not exists birth_date date,
  add column if not exists preferred_sports text[] not null default '{}',
  add column if not exists onboarding_complete boolean not null default false;
