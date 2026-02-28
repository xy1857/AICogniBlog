<template>
  <div class="friend-links" :class="`mode-${props.displayMode}`">
    <h2 class="section-title">友情链接</h2>
    
    <div v-if="props.displayMode === 'cards'" class="links-grid" :style="{ '--columns': props.columns }">
      <a
        v-for="link in props.links"
        :key="link.id"
        :href="link.url"
        target="_blank"
        rel="noopener noreferrer"
        class="link-card"
        :class="`hover-${props.hoverEffect}`"
      >
        <img v-if="props.showLogo && link.logo" :src="link.logo" :alt="link.name" class="link-logo">
        <div class="link-info">
          <h3 class="link-name">{{ link.name }}</h3>
          <p v-if="props.showDescription && link.description" class="link-description">
            {{ link.description }}
          </p>
        </div>
      </a>
    </div>

    <div v-else-if="props.displayMode === 'list'" class="links-list">
      <a
        v-for="link in props.links"
        :key="link.id"
        :href="link.url"
        target="_blank"
        rel="noopener noreferrer"
        class="link-item"
      >
        <div class="link-left">
          <img v-if="props.showLogo && link.logo" :src="link.logo" :alt="link.name" class="link-avatar">
          <div class="link-text">
            <h3 class="link-name">{{ link.name }}</h3>
            <p v-if="props.showDescription && link.description" class="link-description">
              {{ link.description }}
            </p>
          </div>
        </div>
        <el-icon class="link-arrow"><ArrowRight /></el-icon>
      </a>
    </div>

    <div v-else-if="props.displayMode === 'carousel'" class="links-carousel">
      <el-carousel :interval="4000" arrow="hover" height="200px">
        <el-carousel-item v-for="link in props.links" :key="link.id">
          <a
            :href="link.url"
            target="_blank"
            rel="noopener noreferrer"
            class="carousel-link"
          >
            <img v-if="props.showLogo && link.logo" :src="link.logo" :alt="link.name" class="carousel-logo">
            <h3 class="carousel-name">{{ link.name }}</h3>
            <p v-if="props.showDescription && link.description" class="carousel-description">
              {{ link.description }}
            </p>
          </a>
        </el-carousel-item>
      </el-carousel>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ArrowRight } from '@element-plus/icons-vue';
import type { FriendLinksProps } from '@/types/page-builder';

const props = withDefaults(defineProps<FriendLinksProps>(), {
  displayMode: 'cards',
  columns: 3,
  showLogo: true,
  showDescription: true,
  hoverEffect: 'scale',
  links: () => [
    {
      id: '1',
      name: 'Vue.js',
      url: 'https://vuejs.org',
      logo: 'https://vuejs.org/logo.svg',
      description: '渐进式 JavaScript 框架',
    },
    {
      id: '2',
      name: 'Element Plus',
      url: 'https://element-plus.org',
      logo: 'https://element-plus.org/images/element-plus-logo.svg',
      description: 'Vue 3 组件库',
    },
    {
      id: '3',
      name: 'Vite',
      url: 'https://vitejs.dev',
      logo: 'https://vitejs.dev/logo.svg',
      description: '下一代前端构建工具',
    },
    {
      id: '4',
      name: 'TypeScript',
      url: 'https://www.typescriptlang.org',
      logo: 'https://www.typescriptlang.org/favicon-32x32.png',
      description: 'JavaScript 的超集',
    },
  ],
});
</script>

<style scoped>
.friend-links {
  padding: 2rem 0;
}

.section-title {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 2rem;
  text-align: center;
  color: #303133;
}

/* Cards 模式 */
.links-grid {
  display: grid;
  grid-template-columns: repeat(var(--columns), 1fr);
  gap: 1.5rem;
}

.link-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 2rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  text-decoration: none;
  transition: all 0.3s ease;
}

.link-card.hover-scale:hover {
  transform: scale(1.05);
}

.link-card.hover-glow:hover {
  box-shadow: 0 8px 24px rgba(64, 158, 255, 0.3);
}

.link-logo {
  width: 64px;
  height: 64px;
  object-fit: contain;
  margin-bottom: 1rem;
}

.link-info {
  text-align: center;
}

.link-name {
  font-size: 1.125rem;
  font-weight: 600;
  color: #303133;
  margin-bottom: 0.5rem;
}

.link-description {
  font-size: 0.875rem;
  color: #909399;
  line-height: 1.5;
}

/* List 模式 */
.links-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.link-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.5rem;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  text-decoration: none;
  transition: all 0.3s ease;
}

.link-item:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
  transform: translateX(4px);
}

.link-left {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.link-avatar {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  object-fit: contain;
}

.link-text {
  flex: 1;
}

.link-arrow {
  color: #909399;
  font-size: 1.25rem;
}

/* Carousel 模式 */
.links-carousel {
  max-width: 600px;
  margin: 0 auto;
}

.carousel-link {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  text-decoration: none;
  padding: 2rem;
}

.carousel-logo {
  width: 80px;
  height: 80px;
  object-fit: contain;
  margin-bottom: 1rem;
}

.carousel-name {
  font-size: 1.5rem;
  font-weight: 600;
  color: #303133;
  margin-bottom: 0.5rem;
}

.carousel-description {
  font-size: 1rem;
  color: #606266;
  text-align: center;
}

@media (max-width: 1024px) {
  .links-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .links-grid {
    grid-template-columns: 1fr;
  }
}
</style>

