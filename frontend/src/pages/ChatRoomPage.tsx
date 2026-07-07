import { useEffect, useRef, useState, type FormEvent } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { extractErrorMessage, getMessages, leaveRoom, type MessageResponse } from '../api/client'
import { connectSocket, disconnectSocket, sendMessage, subscribeRoom } from '../api/socket'

export default function ChatRoomPage() {
  const { id } = useParams()
  const roomId = Number(id)
  const navigate = useNavigate()
  const [messages, setMessages] = useState<MessageResponse[]>([])
  const [content, setContent] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [connected, setConnected] = useState(false)
  const bottomRef = useRef<HTMLDivElement>(null)

  useEffect(() => {
    let subscription: { unsubscribe: () => void } | null = null
    let cancelled = false

    async function setup() {
      try {
        const history = await getMessages(roomId)
        if (cancelled) return
        setMessages(history)

        await connectSocket()
        if (cancelled) return
        setConnected(true)
        subscription = subscribeRoom(roomId, (message) => {
          setMessages((prev) => [...prev, message])
        })
      } catch (err) {
        setError(extractErrorMessage(err, '채팅방 연결에 실패했습니다.'))
      }
    }

    setup()

    return () => {
      cancelled = true
      subscription?.unsubscribe()
      disconnectSocket()
    }
  }, [roomId])

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' })
  }, [messages])

  function handleSend(e: FormEvent) {
    e.preventDefault()
    if (!content.trim()) return
    sendMessage(roomId, content)
    setContent('')
  }

  async function handleLeave() {
    try {
      await leaveRoom(roomId)
    } finally {
      navigate('/rooms')
    }
  }

  return (
    <div className="chat-room-page">
      <div className="chat-room-header">
        <button onClick={handleLeave}>← 나가기</button>
        <span>{connected ? '연결됨' : '연결 중...'}</span>
      </div>

      {error && <p className="error">{error}</p>}

      <div className="chat-messages">
        {messages.map((message) => (
          <div key={message.id} className="chat-message">
            <b>{message.senderUsername}</b> {message.content}
          </div>
        ))}
        <div ref={bottomRef} />
      </div>

      <form onSubmit={handleSend} className="chat-input-form">
        <input value={content} onChange={(e) => setContent(e.target.value)} placeholder="메시지 입력" />
        <button type="submit" disabled={!connected}>
          전송
        </button>
      </form>
    </div>
  )
}
