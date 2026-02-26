<template>
  <div v-if="article" class="article-detail">
    <el-row :gutter="24">
      <el-col :span="17">
        <!-- 文章头部 -->
        <div class="article-header">
          <div class="article-header-glow" />
          <h1 class="article-title">{{ article.title }}</h1>
          <div class="article-meta">
            <span><el-icon><User /></el-icon> {{ article.author?.nickname }}</span>
            <span><el-icon><Calendar /></el-icon> {{ formatDate(article.publishedAt) }}</span>
            <span><el-icon><View /></el-icon> {{ article.viewCount }} 次阅读</span>
            <span v-if="auth.isLoggedIn" class="like-wrap">
              <el-button
                :type="article.liked ? 'primary' : 'default'"
                text
                size="small"
                @click="toggleLike"
              >
                <el-icon><Like /></el-icon>
                {{ article.likeCount ?? 0 }} 点赞
              </el-button>
            </span>
            <el-tag v-if="article.category" size="small" type="info">{{ article.category.name }}</el-tag>
          </div>
          <div class="article-tags">
            <el-tag v-for="tag in article.tags" :key="tag.id" size="small" class="tag-item">{{ tag.name }}</el-tag>
          </div>
        </div>

        <!-- 文章内容 -->
        <el-card class="article-content">
          <div v-html="article.contentHtml" class="markdown-body" />
        </el-card>

        <!-- 评论区 -->
        <el-card class="comment-section">
          <template #header>
            <span class="comment-header"><el-icon><ChatDotRound /></el-icon> 评论（{{ comments.length }}）</span>
          </template>

          <!-- 发表评论 -->
          <div v-if="auth.isLoggedIn" class="comment-form">
            <el-input
              v-model="commentContent"
              type="textarea"
              :rows="3"
              :placeholder="replyTo ? `回复 @${replyTo.nickname}：` : '写下你的评论...'"
              maxlength="1000"
              show-word-limit
            />
            <div class="comment-actions">
              <el-button v-if="replyTo" size="small" @click="replyTo = null">取消回复</el-button>
              <el-button type="primary" :loading="submitting" @click="submitComment">发表评论</el-button>
            </div>
          </div>
          <el-alert v-else type="info" :closable="false" show-icon>
            <router-link to="/login" class="alert-link">登录</router-link> 后才能发表评论
          </el-alert>

          <!-- 评论列表 -->
          <div class="comment-list">
            <div v-for="comment in comments" :key="comment.id" class="comment-item">
              <div class="comment-user">
                <el-avatar size="small" class="comment-avatar">{{ comment.user?.nickname?.charAt(0) }}</el-avatar>
                <span class="comment-nickname">{{ comment.user?.nickname }}</span>
                <span class="comment-time">{{ formatDate(comment.createdAt) }}</span>
                <el-button text size="small" class="reply-btn" @click="setReply(comment)">回复</el-button>
              </div>
              <div class="comment-content">{{ comment.content }}</div>

              <!-- 子评论 -->
              <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                <div class="comment-user">
                  <el-avatar size="small" class="comment-avatar">{{ reply.user?.nickname?.charAt(0) }}</el-avatar>
                  <span class="comment-nickname">{{ reply.user?.nickname }}</span>
                  <span class="comment-time">{{ formatDate(reply.createdAt) }}</span>
                </div>
                <div class="comment-content">{{ reply.content }}</div>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>

      <!-- 侧边信息 -->
      <el-col :span="7">
        <el-card class="author-card">
          <template #header><b>作者信息</b></template>
          <div class="author-info">
            <div class="author-avatar">{{ article.author?.nickname?.charAt(0) }}</div>
            <p class="author-name">{{ article.author?.nickname }}</p>
            <el-button
              v-if="auth.isLoggedIn && article.author && article.author.id !== auth.userId"
              :type="isFollowingAuthor ? 'info' : 'primary'"
              size="small"
              class="follow-btn"
              @click="toggleFollowAuthor"
            >
              {{ isFollowingAuthor ? '已关注' : '关注' }}
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
  <div v-else-if="loading" class="loading-wrap">
    <el-skeleton :rows="8" animated />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { articleApi } from '@/api/article'
import { commentApi } from '@/api/comment'
import { followApi } from '@/api/follow'
import { useAuthStore } from '@/stores/auth'
import { ElMessage } from 'element-plus'

const route = useRoute()
const auth = useAuthStore()
const article = ref<any>(null)
const comments = ref<any[]>([])
const loading = ref(false)
const commentContent = ref('')
const submitting = ref(false)
const replyTo = ref<{ id: number; nickname: string } | null>(null)
const isFollowingAuthor = ref(false)

function setReply(comment: any) {
  replyTo.value = { id: comment.id, nickname: comment.user?.nickname }
  commentContent.value = ''
}

async function toggleFollowAuthor() {
  const authorId = article.value?.author?.id
  if (!authorId) return
  try {
    if (isFollowingAuthor.value) {
      await followApi.unfollow(authorId)
      isFollowingAuthor.value = false
      ElMessage.success('已取消关注')
    } else {
      await followApi.follow(authorId)
      isFollowingAuthor.value = true
      ElMessage.success('关注成功')
    }
  } catch (e: any) {
    ElMessage.error(e?.response?.data?.message ?? '操作失败')
  }
}

async function toggleLike() {
  if (!article.value) return
  try {
    if (article.value.liked) {
      await articleApi.unlike(Number(route.params.id))
      article.value.liked = false
      article.value.likeCount = Math.max(0, (article.value.likeCount ?? 1) - 1)
    } else {
      await articleApi.like(Number(route.params.id))
      article.value.liked = true
      article.value.likeCount = (article.value.likeCount ?? 0) + 1
    }
  } catch (e: any) {
    ElMessage.error(e?.response?.data?.message ?? '操作失败')
  }
}

async function submitComment() {
  if (!commentContent.value.trim()) return ElMessage.warning('请输入评论内容')
  submitting.value = true
  try {
    await commentApi.submit(Number(route.params.id), {
      content: commentContent.value,
      parentId: replyTo.value?.id,
    })
    ElMessage.success('评论已提交，审核后将显示')
    commentContent.value = ''
    replyTo.value = null
  } finally {
    submitting.value = false
  }
}

function formatDate(date: string) {
  return date ? new Date(date).toLocaleString('zh-CN') : ''
}

onMounted(async () => {
  loading.value = true
  const id = Number(route.params.id)
  try {
    const [articleRes, commentRes] = await Promise.all([
      articleApi.detail(id),
      commentApi.list(id),
    ]) as any[]
    article.value = articleRes.data
    comments.value = commentRes.data
    if (auth.isLoggedIn && article.value?.author?.id && article.value.author.id !== auth.userId) {
      const checkRes = await followApi.checkFollowing(article.value.author.id) as any
      isFollowingAuthor.value = !!checkRes.data
    }
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.article-detail {
  animation: fadeIn 0.4s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}

.article-header {
  position: relative;
  margin-bottom: 28px;
  padding-bottom: 24px;
  border-bottom: 1px solid var(--border);
}

.article-header-glow {
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 120px;
  height: 2px;
  background: var(--gradient-primary);
  border-radius: 2px;
}

.article-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 16px;
  font-family: var(--font-display);
  letter-spacing: 0.5px;
  color: var(--text-primary);
  line-height: 1.3;
}

.article-meta {
  display: flex;
  gap: 20px;
  color: var(--text-muted);
  font-size: 14px;
  margin-bottom: 12px;
  flex-wrap: wrap;
  align-items: center;
}

.article-meta span {
  display: flex;
  align-items: center;
  gap: 6px;
}

.tag-item {
  margin-right: 8px !important;
  margin-bottom: 4px !important;
}

.article-content {
  margin-bottom: 28px;
  border-radius: 16px;
}

.markdown-body {
  line-height: 1.9;
  font-size: 16px;
}

.comment-section {
  border-radius: 16px;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.comment-form {
  margin-bottom: 24px;
}

.comment-actions {
  margin-top: 12px;
  display: flex;
  gap: 12px;
  align-items: center;
}

.alert-link {
  color: var(--primary);
  text-decoration: none;
}

.alert-link:hover {
  text-decoration: underline;
}

.comment-list {
  margin-top: 8px;
}

.comment-item {
  padding: 16px 0;
  border-bottom: 1px solid var(--border);
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-user {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.comment-avatar {
  background: var(--gradient-primary) !important;
}

.comment-nickname {
  font-weight: 600;
  font-size: 14px;
  color: var(--text-primary);
}

.comment-time {
  color: var(--text-muted);
  font-size: 12px;
  margin-left: auto;
}

.reply-btn {
  color: var(--primary) !important;
}

.comment-content {
  font-size: 14px;
  color: var(--text-secondary);
  padding-left: 40px;
  line-height: 1.6;
}

.reply-item {
  margin-left: 40px;
  padding: 12px 0;
  border-top: 1px solid var(--border);
}

.author-card {
  border-radius: 16px;
}

.author-info {
  text-align: center;
  padding: 8px 0;
}
.follow-btn {
  margin-top: 8px;
}

.author-avatar {
  width: 72px;
  height: 72px;
  margin: 0 auto 16px;
  border-radius: 50%;
  background: var(--gradient-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 700;
  color: #fff;
  box-shadow: 0 4px 20px rgba(14, 165, 233, 0.25);
}

.author-name {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.loading-wrap {
  padding: 40px;
}
</style>
