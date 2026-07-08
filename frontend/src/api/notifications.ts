export interface NotificationEvent {
  userId: number
  roomId: number
  unreadCount: number
}

export function subscribeNotifications(onUnread: (event: NotificationEvent) => void): EventSource {
  const accessToken = localStorage.getItem('accessToken')
  const eventSource = new EventSource(`/api/v1/notify/subscribe?token=${accessToken}`)

  eventSource.addEventListener('unread', (e) => {
    onUnread(JSON.parse((e as MessageEvent).data))
  })

  return eventSource
}
