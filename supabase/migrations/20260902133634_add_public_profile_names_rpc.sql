create or replace function public.public_profile_names(p_ids uuid[])
returns table (
  id uuid,
  display_name text
)
language sql
stable
security definer
set search_path = ''
as $function$
  select p.id, p.display_name
  from public.profiles p
  where (select auth.uid()) is not null
    and p.id = any(p_ids);
$function$;

revoke execute on function public.public_profile_names(uuid[]) from public, anon;
grant execute on function public.public_profile_names(uuid[]) to authenticated;
