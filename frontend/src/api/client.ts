import axios from 'axios'

const client = axios.create({
  baseURL: '/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
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

export function extractErrorMessage(error: unknown, fallback: string): string {
  if (axios.isAxiosError(error)) {
    const message = error.response?.data?.message
    if (typeof message === 'string') {
      return message
    }
  }
  return fallback
}
