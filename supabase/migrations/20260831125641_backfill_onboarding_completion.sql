update public.profiles
set onboarding_complete = true
where onboarding_completed_at is not null
  and onboarding_complete = false;
