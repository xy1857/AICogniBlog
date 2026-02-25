<template>
  <div v-if="article" class="article-detail">
    <el-row :gutter="24">
      <el-col :span="17">
        <!-- 文章头部 -->
        <div class="article-header">
          <h1>{{ article.title }}</h1>
          <div class="article-meta">
            <span>作者：{{ article.author?.nickname }}</span>
            <span>发布于 {{ formatDate(article.publishedAt) }}</span>
            <span><el-icon><View /></el-icon> {{ article.viewCount }} 次阅读</span>
            <el-tag v-if="article.category" size="small">{{ article.category.name }}</el-tag>
          </div>
          <div class="article-tags">
            <el-tag v-for="tag in article.tags" :key="tag.id" size="small" style="margin-right:6px">{{ tag.name }}</el-tag>
          </div>
        </div>

        <!-- 文章内容 -->
        <el-card class="article-content">
          <div v-html="article.contentHtml" class="markdown-body" />
        </el-card>

        <!-- 评论区 -->
        <el-card class="comment-section">
          <template #header><b>评论（{{ comments.length }}）</b></template>

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
            <div style="margin-top:8px;display:flex;gap:8px;align-items:center">
              <el-button v-if="replyTo" size="small" @click="replyTo = null">取消回复</el-button>
              <el-button type="primary" :loading="submitting" @click="submitComment">发表评论</el-button>
            </div>
          </div>
          <el-alert v-else type="info" :closable="false" show-icon>
            <router-link to="/login">登录</router-link> 后才能发表评论
          </el-alert>

          <!-- 评论列表 -->
          <div class="comment-list">
            <div v-for="comment in comments" :key="comment.id" class="comment-item">
              <div class="comment-user">
                <el-avatar size="small">{{ comment.user?.nickname?.charAt(0) }}</el-avatar>
                <span class="comment-nickname">{{ comment.user?.nickname }}</span>
                <span class="comment-time">{{ formatDate(comment.createdAt) }}</span>
                <el-button text size="small" @click="setReply(comment)">回复</el-button>
              </div>
              <div class="comment-content">{{ comment.content }}</div>

              <!-- 子评论 -->
              <div v-for="reply in comment.replies" :key="reply.id" class="reply-item">
                <div class="comment-user">
                  <el-avatar size="small">{{ reply.user?.nickname?.charAt(0) }}</el-avatar>
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
        <el-card>
          <template #header><b>作者信息</b></template>
          <div class="author-info">
            <el-avatar :size="60">{{ article.author?.nickname?.charAt(0) }}</el-avatar>
            <p>{{ article.author?.nickname }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
  <div v-else-if="loading" style="padding:40px">
    <el-skeleton :rows="8" animated />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { articleApi } from '@/api/article'
import { commentApi } from '@/api/comment'
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

function setReply(comment: any) {
  replyTo.value = { id: comment.id, nickname: comment.user?.nickname }
  commentContent.value = ''
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
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.article-header { margin-bottom: 20px; }
.article-header h1 { font-size: 28px; margin-bottom: 12px; }
.article-meta { display: flex; gap: 16px; color: #999; font-size: 13px; margin-bottom: 10px; flex-wrap: wrap; align-items: center; }
.article-content { margin-bottom: 20px; }
.markdown-body { line-height: 1.8; }
.comment-section { margin-top: 20px; }
.comment-form { margin-bottom: 20px; }
.comment-item { padding: 12px 0; border-bottom: 1px solid #f5f5f5; }
.comment-user { display: flex; align-items: center; gap: 8px; margin-bottom: 6px; }
.comment-nickname { font-weight: 500; font-size: 14px; }
.comment-time { color: #999; font-size: 12px; }
.comment-content { font-size: 14px; color: #333; padding-left: 32px; }
.reply-item { margin-left: 32px; padding: 8px 0; border-top: 1px solid #fafafa; }
.author-info { text-align: center; }
</style>
