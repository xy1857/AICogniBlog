import http from './http'

export const commentApi = {
  list: (articleId: number) => http.get(`/articles/${articleId}/comments`),
  latest: (params?: { limit?: number }) => http.get('/comments/latest', { params }),
  getLatestComments: (limit: number = 10) => http.get('/comments/latest', { params: { limit } }),

  submit: (articleId: number, data: { content: string; parentId?: number }) =>
    http.post(`/articles/${articleId}/comments`, data),

  adminList: (params: { page?: number; size?: number; status?: number }) =>
    http.get('/admin/comments', { params }),

  approve: (id: number) => http.put(`/admin/comments/${id}/approve`),

  reject: (id: number) => http.put(`/admin/comments/${id}/reject`),

  delete: (id: number) => http.delete(`/admin/comments/${id}`),

  aiReview: (id: number) => http.post(`/admin/comments/${id}/ai-review`),
}
