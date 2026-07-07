import axios from 'axios'

const client = axios.create({
  baseURL: '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
})

client.interceptors.request.use((config) => {
  const accessToken = localStorage.getItem('accessToken')
  if (accessToken) {
    config.headers.Authorization = `Bearer ${accessToken}`
  }
  return config
})

export interface TokenResponse {
  accessToken: string
  refreshToken: string
  tokenType: string
}

export interface SignupPayload {
  username: string
  email: string
  password: string
}

export interface LoginPayload {
  username: string
  password: string
}

export async function signup(payload: SignupPayload) {
  const { data } = await client.post('/auth/signup', payload)
  return data
}

export async function login(payload: LoginPayload) {
  const { data } = await client.post<TokenResponse>('/auth/login', payload)
  return data
}

export async function refresh(refreshToken: string) {
  const { data } = await client.post<TokenResponse>('/auth/refresh', { refreshToken })
  return data
}

export interface RoomResponse {
  id: number
  name: string
  createdBy: number
  createdAt: string
}

export interface MessageResponse {
  id: number
  roomId: number
  senderId: number
  senderUsername: string
  content: string
  sentAt: string
}

export async function createRoom(name: string) {
  const { data } = await client.post<RoomResponse>('/rooms', { name })
  return data
}

export async function listRooms() {
  const { data } = await client.get<RoomResponse[]>('/rooms')
  return data
}

export async function joinRoom(roomId: number) {
  await client.post(`/rooms/${roomId}/join`)
}

export async function leaveRoom(roomId: number) {
  await client.delete(`/rooms/${roomId}/leave`)
}

export async function getMessages(roomId: number) {
  const { data } = await client.get<MessageResponse[]>(`/rooms/${roomId}/messages`)
  return data
}

export function extractErrorMessage(error: unknown, fallback: string): string {
  if (axios.isAxiosError(error)) {
    const message = error.response?.data?.message
    if (typeof message === 'string') {
      return message
    }
  }
  return fallback
}
