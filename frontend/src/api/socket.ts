import { Client, type StompSubscription } from '@stomp/stompjs'
import SockJS from 'sockjs-client'
import type { MessageResponse } from './client'

let client: Client | null = null

export function connectSocket(): Promise<void> {
  return new Promise((resolve, reject) => {
    const accessToken = localStorage.getItem('accessToken')

    client = new Client({
      webSocketFactory: () => new SockJS('/ws'),
      connectHeaders: {
        Authorization: `Bearer ${accessToken}`,
      },
      reconnectDelay: 5000,
      onConnect: () => resolve(),
      onStompError: (frame) => reject(new Error(frame.headers['message'] ?? 'STOMP 연결 오류')),
    })

    client.activate()
  })
}

export function disconnectSocket() {
  client?.deactivate()
  client = null
}

export function subscribeRoom(roomId: number, onMessage: (message: MessageResponse) => void): StompSubscription {
  if (!client) {
    throw new Error('소켓이 연결되어 있지 않습니다.')
  }
  return client.subscribe(`/sub/room/${roomId}`, (frame) => {
    onMessage(JSON.parse(frame.body) as MessageResponse)
  })
}

export function sendMessage(roomId: number, content: string) {
  if (!client) {
    throw new Error('소켓이 연결되어 있지 않습니다.')
  }
  client.publish({
    destination: '/pub/message',
    body: JSON.stringify({ roomId, content }),
  })
}
