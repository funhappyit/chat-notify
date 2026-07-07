import axios from 'axios'
import { useEffect, useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { createRoom, extractErrorMessage, joinRoom, listRooms, type RoomResponse } from '../api/client'

export default function RoomListPage() {
  const navigate = useNavigate()
  const [rooms, setRooms] = useState<RoomResponse[]>([])
  const [newRoomName, setNewRoomName] = useState('')
  const [error, setError] = useState<string | null>(null)

  async function loadRooms() {
    try {
      setRooms(await listRooms())
    } catch (err) {
      setError(extractErrorMessage(err, '채팅방 목록을 불러오지 못했습니다.'))
    }
  }

  useEffect(() => {
    loadRooms()
  }, [])

  async function handleCreate(e: FormEvent) {
    e.preventDefault()
    setError(null)
    try {
      const room = await createRoom(newRoomName)
      setNewRoomName('')
      await loadRooms()
      navigate(`/rooms/${room.id}`)
    } catch (err) {
      setError(extractErrorMessage(err, '채팅방 생성에 실패했습니다.'))
    }
  }

  async function handleJoin(roomId: number) {
    setError(null)
    try {
      await joinRoom(roomId)
      navigate(`/rooms/${roomId}`)
    } catch (err) {
      if (axios.isAxiosError(err) && err.response?.status === 409) {
        navigate(`/rooms/${roomId}`)
        return
      }
      setError(extractErrorMessage(err, '채팅방 입장에 실패했습니다.'))
    }
  }

  return (
    <div className="room-list-page">
      <h1>채팅방 목록</h1>

      <form onSubmit={handleCreate} className="room-create-form">
        <input
          value={newRoomName}
          onChange={(e) => setNewRoomName(e.target.value)}
          placeholder="새 채팅방 이름"
          required
        />
        <button type="submit">만들기</button>
      </form>

      {error && <p className="error">{error}</p>}

      <ul className="room-list">
        {rooms.map((room) => (
          <li key={room.id}>
            <span>{room.name}</span>
            <button onClick={() => handleJoin(room.id)}>입장</button>
          </li>
        ))}
      </ul>
    </div>
  )
}
