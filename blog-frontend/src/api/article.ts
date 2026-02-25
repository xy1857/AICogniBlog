import http from './http'

export interface ArticleRequest {
  title: string
  summary?: string
  contentMd: string
  coverUrl?: string
  categoryId?: number
  tagIds?: number[]
  status: number
}

export const articleApi = {
  list: (params: { page?: number; size?: number; categoryId?: number; tagId?: number; keyword?: string }) =>
    http.get('/articles', { params }),

  detail: (id: number) => http.get(`/articles/${id}`),

  categories: () => http.get('/categories'),

  tags: () => http.get('/tags'),

  create: (data: ArticleRequest) => http.post('/articles', data),

  update: (id: number, data: ArticleRequest) => http.put(`/articles/${id}`, data),

  delete: (id: number) => http.delete(`/articles/${id}`),

  getForEdit: (id: number) => http.get(`/articles/${id}/edit`),
}
