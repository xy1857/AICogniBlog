<template>
  <div class="interactive-widget" :class="`theme-${props.theme}`">
    <div v-if="props.widgetType === 'guestbook'" class="guestbook">
      <h2 class="widget-title">留言板</h2>
      
      <div class="message-form">
        <el-input
          v-model="newMessage.name"
          placeholder="你的昵称"
          class="form-input"
        />
        <el-input
          v-model="newMessage.content"
          type="textarea"
          :rows="3"
          placeholder="留下你的足迹..."
          class="form-input"
        />
        <div class="form-actions">
          <div v-if="props.enableEmoji" class="emoji-picker">
            <span v-for="emoji in emojis" :key="emoji" class="emoji" @click="addEmoji(emoji)">
              {{ emoji }}
            </span>
          </div>
          <el-button type="primary" @click="submitMessage">发送</el-button>
        </div>
      </div>

      <div class="messages-list">
        <div
          v-for="message in displayMessages"
          :key="message.id"
          class="message-item"
        >
          <img
            v-if="props.enableAvatar"
            :src="message.avatar"
            :alt="message.name"
            class="message-avatar"
          >
          <div class="message-content">
            <div class="message-header">
              <span class="message-name">{{ message.name }}</span>
              <span class="message-time">{{ formatTime(message.time) }}</span>
            </div>
            <p class="message-text">{{ message.content }}</p>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="props.widgetType === 'comments'" class="comments">
      <h2 class="widget-title">最新评论</h2>
      
      <div class="comments-list">
        <div
          v-for="comment in recentComments"
          :key="comment.id"
          class="comment-item"
        >
          <img
            v-if="props.enableAvatar"
            :src="comment.avatar"
            :alt="comment.author"
            class="comment-avatar"
          >
          <div class="comment-content">
            <div class="comment-header">
              <span class="comment-author">{{ comment.author }}</span>
              <span class="comment-article">评论了《{{ comment.article }}》</span>
            </div>
            <p class="comment-text">{{ comment.content }}</p>
            <span class="comment-time">{{ formatTime(comment.time) }}</span>
          </div>
        </div>
      </div>
    </div>

    <div v-else-if="props.widgetType === 'visitor-map'" class="visitor-map">
      <h2 class="widget-title">访客地图</h2>
      
      <div class="map-container">
        <div class="map-placeholder">
          <el-icon class="map-icon"><Location /></el-icon>
          <p>访客地图加载中...</p>
        </div>
      </div>

      <div class="visitor-stats">
        <div class="stat-item">
          <div class="stat-value">{{ visitorStats.total }}</div>
          <div class="stat-label">总访问量</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ visitorStats.today }}</div>
          <div class="stat-label">今日访问</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ visitorStats.countries }}</div>
          <div class="stat-label">访问国家</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { Location } from '@element-plus/icons-vue';
import { ElMessage } from 'element-plus';
import { guestbookApi } from '@/api/guestbook';
import { commentApi } from '@/api/comment';
import { statsApi } from '@/api/stats';
import type { InteractiveWidgetProps } from '@/types/page-builder';

const props = withDefaults(defineProps<InteractiveWidgetProps>(), {
  widgetType: 'guestbook',
  theme: 'auto',
  enableEmoji: true,
  enableAvatar: true,
  maxItems: 10,
});

const newMessage = ref({
  name: '',
  content: '',
});

const emojis = ['😊', '👍', '❤️', '🎉', '🔥', '💯', '✨', '🚀'];

const messages = ref<any[]>([]);
const recentComments = ref<any[]>([]);
const visitorStats = ref({
  total: 0,
  today: 0,
  countries: 0,
});
const loading = ref(false);

// 加载留言板数据
const loadMessages = async () => {
  loading.value = true;
  try {
    const response = await guestbookApi.getMessages({
      page: 1,
      size: props.maxItems,
    });
    
    messages.value = response.data.content.map((msg: any) => ({
      id: msg.id,
      name: msg.name,
      avatar: msg.avatarUrl || `https://api.dicebear.com/7.x/avataaars/svg?seed=${msg.id}`,
      content: msg.content,
      time: msg.createdAt,
    }));
  } catch (error) {
    console.error('Failed to load messages:', error);
    // 降级到模拟数据
    messages.value = getMockMessages();
  } finally {
    loading.value = false;
  }
};

// 加载评论数据
const loadComments = async () => {
  loading.value = true;
  try {
    const response = await commentApi.getLatestComments(props.maxItems);
    
    recentComments.value = response.data.map((comment: any) => ({
      id: comment.id,
      author: comment.username || comment.user?.nickname || '匿名用户',
      avatar: `https://api.dicebear.com/7.x/avataaars/svg?seed=${comment.userId}`,
      article: comment.articleTitle || '未知文章',
      content: comment.content,
      time: comment.createdAt,
    }));
  } catch (error) {
    console.error('Failed to load comments:', error);
    // 降级到模拟数据
    recentComments.value = getMockComments();
  } finally {
    loading.value = false;
  }
};

// 加载统计数据
const loadStats = async () => {
  loading.value = true;
  try {
    const response = await statsApi.getStats();
    
    visitorStats.value = {
      total: response.data.totalViews || 0,
      today: response.data.todayViews || 0,
      countries: response.data.visitorCountries || 0,
    };
  } catch (error) {
    console.error('Failed to load stats:', error);
    // 降级到模拟数据
    visitorStats.value = {
      total: 12580,
      today: 156,
      countries: 23,
    };
  } finally {
    loading.value = false;
  }
};

// 模拟留言数据
const getMockMessages = () => [
  {
    id: 1,
    name: '张三',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=1',
    content: '很棒的博客，学到了很多！',
    time: new Date(Date.now() - 3600000).toISOString(),
  },
  {
    id: 2,
    name: '李四',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=2',
    content: '期待更多精彩内容 🎉',
    time: new Date(Date.now() - 7200000).toISOString(),
  },
  {
    id: 3,
    name: '王五',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=3',
    content: '写得真好，收藏了！',
    time: new Date(Date.now() - 86400000).toISOString(),
  },
];

// 模拟评论数据
const getMockComments = () => [
  {
    id: 1,
    author: '小明',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=4',
    article: 'Vue 3 组合式 API 最佳实践',
    content: '这篇文章解决了我的问题，感谢分享！',
    time: new Date(Date.now() - 1800000).toISOString(),
  },
  {
    id: 2,
    author: '小红',
    avatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=5',
    article: 'TypeScript 高级类型系统详解',
    content: '讲解得很清晰，学习了！',
    time: new Date(Date.now() - 5400000).toISOString(),
  },
];

const displayMessages = computed(() => {
  return messages.value.slice(0, props.maxItems);
});

const formatTime = (time: string) => {
  const date = new Date(time);
  const now = new Date();
  const diff = now.getTime() - date.getTime();
  
  const minutes = Math.floor(diff / 60000);
  const hours = Math.floor(diff / 3600000);
  const days = Math.floor(diff / 86400000);
  
  if (minutes < 1) return '刚刚';
  if (minutes < 60) return `${minutes}分钟前`;
  if (hours < 24) return `${hours}小时前`;
  if (days < 7) return `${days}天前`;
  
  return date.toLocaleDateString('zh-CN');
};

const addEmoji = (emoji: string) => {
  newMessage.value.content += emoji;
};

const submitMessage = async () => {
  if (!newMessage.value.name || !newMessage.value.content) {
    ElMessage.warning('请填写昵称和留言内容');
    return;
  }
  
  try {
    await guestbookApi.postMessage({
      name: newMessage.value.name,
      content: newMessage.value.content,
    });
    
    // 重新加载留言列表
    await loadMessages();
    
    // 清空表单
    newMessage.value = { name: '', content: '' };
    
    ElMessage.success('留言成功');
  } catch (error) {
    console.error('Failed to submit message:', error);
    ElMessage.error('留言失败，请稍后重试');
  }
};

onMounted(() => {
  // 根据组件类型加载对应数据
  if (props.widgetType === 'guestbook') {
    loadMessages();
  } else if (props.widgetType === 'comments') {
    loadComments();
  } else if (props.widgetType === 'visitor-map') {
    loadStats();
  }
});
</script>

<style scoped>
.interactive-widget {
  padding: 2rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.theme-dark {
  background: #1a1a1a;
  color: #e4e7ed;
}

.widget-title {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
  color: #303133;
}

.theme-dark .widget-title {
  color: #e4e7ed;
}

/* 留言板 */
.message-form {
  margin-bottom: 2rem;
}

.form-input {
  margin-bottom: 1rem;
}

.form-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.emoji-picker {
  display: flex;
  gap: 0.5rem;
}

.emoji {
  font-size: 1.5rem;
  cursor: pointer;
  transition: transform 0.2s ease;
}

.emoji:hover {
  transform: scale(1.2);
}

.messages-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.message-item {
  display: flex;
  gap: 1rem;
  padding: 1rem;
  background: #f5f7fa;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.message-item:hover {
  background: #ecf5ff;
  transform: translateX(4px);
}

.message-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  flex-shrink: 0;
}

.message-content {
  flex: 1;
}

.message-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
}

.message-name {
  font-weight: 600;
  color: #303133;
}

.message-time {
  font-size: 0.875rem;
  color: #909399;
}

.message-text {
  color: #606266;
  line-height: 1.6;
}

/* 评论列表 */
.comments-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.comment-item {
  display: flex;
  gap: 1rem;
  padding: 1rem;
  border-left: 3px solid #409EFF;
  background: #f5f7fa;
  border-radius: 8px;
}

.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  flex-shrink: 0;
}

.comment-content {
  flex: 1;
}

.comment-header {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
}

.comment-author {
  font-weight: 600;
  color: #303133;
}

.comment-article {
  color: #409EFF;
  font-size: 0.875rem;
}

.comment-text {
  color: #606266;
  line-height: 1.6;
  margin-bottom: 0.5rem;
}

.comment-time {
  font-size: 0.875rem;
  color: #909399;
}

/* 访客地图 */
.map-container {
  margin-bottom: 1.5rem;
}

.map-placeholder {
  height: 300px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
}

.map-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

.visitor-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
}

.stat-item {
  text-align: center;
  padding: 1.5rem;
  background: #f5f7fa;
  border-radius: 8px;
}

.stat-value {
  font-size: 2rem;
  font-weight: 700;
  color: #409EFF;
  margin-bottom: 0.5rem;
}

.stat-label {
  font-size: 0.875rem;
  color: #909399;
}

@media (max-width: 640px) {
  .visitor-stats {
    grid-template-columns: 1fr;
  }
}
</style>

