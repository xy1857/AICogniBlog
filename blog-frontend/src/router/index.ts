import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // 博客前台
    {
      path: '/',
      component: () => import('@/components/layout/BlogLayout.vue'),
      children: [
        { path: '', name: 'Home', component: () => import('@/views/blog/HomeView.vue') },
        { path: 'article/:id', name: 'ArticleDetail', component: () => import('@/views/blog/ArticleDetailView.vue') },
        { path: 'profile', name: 'Profile', component: () => import('@/views/blog/ProfileView.vue'), meta: { requireAuth: true } },
      ],
    },
    // 认证
    { path: '/login', name: 'Login', component: () => import('@/views/auth/LoginView.vue') },
    { path: '/register', name: 'Register', component: () => import('@/views/auth/RegisterView.vue') },
    // 管理后台
    {
      path: '/admin',
      component: () => import('@/components/layout/AdminLayout.vue'),
      meta: { requireAuth: true, requireAdmin: true },
      children: [
        { path: '', redirect: '/admin/articles' },
        { path: 'articles', name: 'AdminArticles', component: () => import('@/views/admin/ArticleListView.vue') },
        { path: 'articles/create', name: 'ArticleCreate', component: () => import('@/views/admin/ArticleEditView.vue') },
        { path: 'articles/edit/:id', name: 'ArticleEdit', component: () => import('@/views/admin/ArticleEditView.vue') },
        { path: 'comments', name: 'AdminComments', component: () => import('@/views/admin/CommentManageView.vue') },
        { path: 'users', name: 'AdminUsers', component: () => import('@/views/admin/UserManageView.vue') },
      ],
    },
  ],
})

router.beforeEach((to) => {
  const auth = useAuthStore()
  if (to.meta.requireAuth && !auth.isLoggedIn) return '/login'
  if (to.meta.requireAdmin && !auth.isAdmin) return '/'
})

export default router
