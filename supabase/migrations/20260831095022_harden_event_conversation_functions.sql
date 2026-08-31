-- The trigger function must not be callable through the PostgREST RPC API.
revoke execute on function public.create_event_conversation() from public, anon, authenticated;

-- Conversation read helpers intentionally use the caller's RLS context.
alter function public.can_access_event_conversation(uuid) security invoker;
alter function public.my_event_conversations() security invoker;

revoke execute on function public.my_event_conversations() from public, anon;
grant execute on function public.my_event_conversations() to authenticated;
