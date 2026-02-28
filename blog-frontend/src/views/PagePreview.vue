<template>
  <div class="page-preview" :style="pageStyle">
    <component
      v-for="comp in components"
      :key="comp.id"
      :is="getComponentByType(comp.type)"
      v-bind="comp.props"
      :style="comp.style"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import HeroBanner from '@/components/page-builder/widgets/HeroBanner.vue';
import ArticleGrid from '@/components/page-builder/widgets/ArticleGrid.vue';
import CategoryNav from '@/components/page-builder/widgets/CategoryNav.vue';
import FriendLinks from '@/components/page-builder/widgets/FriendLinks.vue';
import InteractiveWidget from '@/components/page-builder/widgets/InteractiveWidget.vue';
import type { PageConfig, ComponentType } from '@/types/page-builder';

// 支持通过 props 传递用户名（用于主页嵌入）
const props = defineProps<{
  username?: string;
}>();

const route = useRoute();
const pageConfig = ref<PageConfig | null>(null);

const components = computed(() => {
  return pageConfig.value?.components.sort((a, b) => a.position - b.position) || [];
});

const pageStyle = computed(() => {
  if (!pageConfig.value) return {};
  const theme = pageConfig.value.theme;
  return {
    backgroundColor: theme.backgroundColor,
    color: theme.textColor,
    fontFamily: theme.fontFamily,
    '--primary-color': theme.primaryColor,
    '--border-radius': theme.borderRadius,
  };
});

const getComponentByType = (type: ComponentType) => {
  const componentMap = {
    HeroBanner,
    ArticleGrid,
    CategoryNav,
    FriendLinks,
    InteractiveWidget,
  };
  return componentMap[type];
};

const loadPageConfig = async () => {
  // 优先使用 props 传递的用户名，其次使用路由参数
  const username = props.username || (route.params.username as string);
  
  if (username) {
    // 加载指定用户的发布页面
    try {
      // TODO: 从后端API加载
      // const config = await getPublicPage(username);
      // pageConfig.value = config;
      
      // 临时方案：从 localStorage 加载（演示用）
      const saved = localStorage.getItem(`pageConfig_${username}`);
      if (saved) {
        pageConfig.value = JSON.parse(saved);
      } else {
        // 如果是当前用户，尝试加载默认配置
        const defaultSaved = localStorage.getItem('pageConfig');
        if (defaultSaved) {
          pageConfig.value = JSON.parse(defaultSaved);
        }
      }
    } catch (e) {
      console.error('Failed to load page config:', e);
    }
  } else {
    // 加载当前用户的配置（预览模式）
    const saved = localStorage.getItem('pageConfig');
    if (saved) {
      try {
        pageConfig.value = JSON.parse(saved);
      } catch (e) {
        console.error('Failed to load page config:', e);
      }
    }
  }
};

onMounted(() => {
  loadPageConfig();
});

// 监听 props.username 变化，重新加载配置
watch(() => props.username, () => {
  if (props.username) {
    loadPageConfig();
  }
});
</script>

<style scoped>
.page-preview {
  min-height: 100vh;
  width: 100%;
}
</style>

