import http from './http'

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  password: string
  email: string
}

export interface LoginResponse {
  token: string
  userId: number
  username: string
  nickname: string
  role: number
}

export const authApi = {
  login: (data: LoginRequest) =>
    http.post<any, { data: LoginResponse }>('/auth/login', data),

  register: (data: RegisterRequest) =>
    http.post('/auth/register', data),

  logout: () => http.post('/auth/logout'),

  getPublicKey: () =>
    http.get<any, { data: { publicKey: string } }>('/auth/public-key'),
}
