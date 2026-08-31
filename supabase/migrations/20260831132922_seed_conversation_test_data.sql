do $$
declare
  john_id constant uuid := '10000000-0000-4000-8000-000000000007';
  pierre_id constant uuid := 'fe3542f1-c579-4071-bd7b-4d72a6296654';
  target_event_id constant uuid := '20000000-0000-4000-8000-000000000006';
  conversation_id uuid;
begin
  insert into auth.users (
    id,
    aud,
    role,
    email,
    encrypted_password,
    email_confirmed_at,
    raw_app_meta_data,
    raw_user_meta_data,
    created_at,
    updated_at,
    is_sso_user,
    is_anonymous
  )
  values (
    john_id,
    'authenticated',
    'authenticated',
    'john@fitmeet.example',
    crypt('John1234!', gen_salt('bf')),
    now(),
    '{"provider":"email","providers":["email"]}'::jsonb,
    '{"display_name":"John"}'::jsonb,
    now(),
    now(),
    false,
    false
  )
  on conflict (id) do nothing;

  insert into public.profiles (
    id,
    display_name,
    birth_date,
    preferred_sports,
    onboarding_complete
  )
  values (
    john_id,
    'John',
    '1990-01-01',
    array['volleyball']::text[],
    true
  )
  on conflict (id) do update set
    display_name = excluded.display_name,
    birth_date = excluded.birth_date,
    preferred_sports = excluded.preferred_sports,
    onboarding_complete = excluded.onboarding_complete;

  update public.profiles
  set display_name = 'Pierre', onboarding_complete = true
  where id = pierre_id;

  insert into public.event_participants (event_id, user_id)
  values (target_event_id, pierre_id), (target_event_id, john_id)
  on conflict (event_id, user_id) do nothing;

  select id into conversation_id
  from public.event_conversations
  where event_id = target_event_id;

  if conversation_id is null then
    raise exception 'No conversation found for event %', target_event_id;
  end if;

  insert into public.conversation_messages (
    id,
    conversation_id,
    sender_id,
    content,
    created_at
  )
  values
    (
      '30000000-0000-4000-8000-000000000001',
      conversation_id,
      pierre_id,
      'Salut John, prêt pour le volley ?',
      now() - interval '3 minutes'
    ),
    (
      '30000000-0000-4000-8000-000000000002',
      conversation_id,
      john_id,
      'Oui, à dimanche !',
      now() - interval '2 minutes'
    ),
    (
      '30000000-0000-4000-8000-000000000003',
      conversation_id,
      pierre_id,
      'Parfait, à bientôt.',
      now() - interval '1 minute'
    )
  on conflict (id) do nothing;
end;
$$;
