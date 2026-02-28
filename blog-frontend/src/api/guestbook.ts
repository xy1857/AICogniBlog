import http from './http';

export interface GuestbookMessage {
  id: number;
  userId?: number;
  name: string;
  email?: string;
  content: string;
  avatarUrl?: string;
  createdAt: string;
}

export interface GuestbookRequest {
  name: string;
  email?: string;
  content: string;
}

export const guestbookApi = {
  // 获取留言列表
  getMessages: (params: { page?: number; size?: number }) =>
    http.get<{ content: GuestbookMessage[]; totalElements: number }>('/guestbook', { params }),
  
  // 发表留言
  postMessage: (data: GuestbookRequest) =>
    http.post('/guestbook', data),
  
  // 删除留言（管理员）
  deleteMessage: (id: number) =>
    http.delete(`/guestbook/${id}`),
};

