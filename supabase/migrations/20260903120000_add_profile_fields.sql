alter table profiles
    add column if not exists city text,
    add column if not exists bio text,
    add column if not exists sport_levels jsonb not null default '[]'::jsonb;
