begin;

-- Early onboarding versions stored translated labels. Convert them once to
-- the stable sport slugs used by the profile and activity screens.
update public.profiles profile
set preferred_sports = array(
  select case lower(sport)
    when 'football' then 'football'
    when 'basket' then 'basketball'
    when 'basketball' then 'basketball'
    when 'volley' then 'volleyball'
    when 'volleyball' then 'volleyball'
    when 'tennis' then 'tennis'
    when 'badminton' then 'badminton'
    when 'course' then 'running'
    when 'running' then 'running'
    when 'vélo' then 'cycling'
    when 'cycling' then 'cycling'
    when 'randonnée' then 'hiking'
    when 'hiking' then 'hiking'
    else sport
  end
  from unnest(profile.preferred_sports) as sport
)
where profile.preferred_sports && array[
  'Football', 'Basket', 'Volley', 'Tennis', 'Badminton', 'Course', 'Vélo', 'Randonnée'
]::text[];

commit;
