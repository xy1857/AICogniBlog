import http from './http'

export const subscriptionApi = {
  list: () => http.get('/subscriptions'),

  subscribe: (data: { targetType: string; targetId: number }) =>
    http.post('/subscriptions', data),

  unsubscribe: (params: { targetType: string; targetId: number }) =>
    http.delete('/subscriptions', { params }),

  articles: (params: { page?: number; size?: number }) =>
    http.get('/subscriptions/articles', { params }),
}
