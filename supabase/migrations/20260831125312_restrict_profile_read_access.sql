drop policy if exists "profiles are readable by authenticated users"
  on public.profiles;

create policy "users can read their own profile"
  on public.profiles
  for select
  to authenticated
  using ((select auth.uid()) = id);
