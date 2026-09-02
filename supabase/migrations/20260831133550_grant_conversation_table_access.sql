grant select on table
  public.event_conversations,
  public.events,
  public.conversation_messages
to authenticated;

grant insert on table public.conversation_messages to authenticated;
