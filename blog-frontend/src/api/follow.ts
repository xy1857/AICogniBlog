import http from './http'

export const followApi = {
  follow: (userId: number) => http.post(`/follows/${userId}`),

  unfollow: (userId: number) => http.delete(`/follows/${userId}`),

  listFollowing: (params: { page?: number; size?: number }) =>
    http.get('/follows/following', { params }),

  checkFollowing: (userId: number) => http.get(`/follows/check/${userId}`),
}
