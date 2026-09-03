-- Supports sport lookups and completes foreign-key indexing for user_sports.
create index if not exists user_sports_sport_slug_idx on public.user_sports (sport_slug);
