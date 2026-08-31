grant select (id, display_name, birth_date, preferred_sports, onboarding_complete)
  on table public.profiles
  to authenticated;

grant update (display_name, birth_date, preferred_sports, onboarding_complete)
  on table public.profiles
  to authenticated;
