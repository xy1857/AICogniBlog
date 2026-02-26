import http from './http'

export const userApi = {
  getProfile: () => http.get('/user/profile'),

  updateProfile: (data: { nickname?: string; bio?: string; avatarBase64?: string }) =>
    http.put('/user/profile', data),

  updatePassword: (data: { oldPassword: string; newPassword: string }) =>
    http.put('/user/password', data),

  myComments: (params: { page?: number; size?: number }) =>
    http.get('/user/comments', { params }),

  myLikes: (params: { page?: number; size?: number }) =>
    http.get('/user/likes', { params }),

  footprints: (params: { page?: number; size?: number }) =>
    http.get('/user/footprints', { params }),

  adminList: (params: { page?: number; size?: number; keyword?: string }) =>
    http.get('/admin/users', { params }),

  adminUpdateStatus: (id: number, status: number) =>
    http.put(`/admin/users/${id}/status`, { status }),

  adminUpdateRole: (id: number, role: number) =>
    http.put(`/admin/users/${id}/role`, { role }),

  adminResetPassword: (id: number) =>
    http.put(`/admin/users/${id}/reset-password`),
}
