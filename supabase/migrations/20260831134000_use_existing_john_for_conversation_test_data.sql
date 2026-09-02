do $$
declare
  wrong_john_id constant uuid := '10000000-0000-4000-8000-000000000007';
  pierre_id constant uuid := 'fe3542f1-c579-4071-bd7b-4d72a6296654';
  target_event_id constant uuid := '20000000-0000-4000-8000-000000000006';
  conversation_id uuid;
  existing_john_id uuid;
begin
  select u.id into existing_john_id
  from auth.users u
  join public.profiles p on p.id = u.id
  where u.id <> wrong_john_id
    and lower(u.raw_user_meta_data ->> 'display_name') = 'john'
  order by u.created_at
  limit 1;

  if existing_john_id is null then
    raise exception 'No existing John account found';
  end if;

  select id into conversation_id
  from public.event_conversations
  where event_id = target_event_id;

  if conversation_id is null then
    raise exception 'No conversation found for event %', target_event_id;
  end if;

  delete from public.conversation_messages
  where id in (
    '30000000-0000-4000-8000-000000000001',
    '30000000-0000-4000-8000-000000000002',
    '30000000-0000-4000-8000-000000000003'
  );

  delete from public.event_participants
  where event_id = target_event_id
    and user_id = wrong_john_id;

  delete from public.profiles
  where id = wrong_john_id;

  delete from auth.users
  where id = wrong_john_id;

  insert into public.event_participants (event_id, user_id)
  values (target_event_id, pierre_id), (target_event_id, existing_john_id)
  on conflict (event_id, user_id) do nothing;

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
      existing_john_id,
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
