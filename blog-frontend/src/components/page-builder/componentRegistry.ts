import type { ComponentMeta, ComponentType } from '@/types/page-builder';

// 组件注册表
export const componentRegistry: Record<ComponentType, ComponentMeta> = {
  HeroBanner: {
    type: 'HeroBanner',
    name: 'Hero横幅',
    icon: 'Picture',
    description: '大型横幅，支持图片/视频/渐变背景',
    defaultProps: {
      backgroundType: 'gradient',
      gradientColors: ['#667eea', '#764ba2'],
      title: '欢迎来到我的博客',
      subtitle: '分享技术，记录生活',
      ctaText: '开始阅读',
      ctaLink: '#articles',
      height: '500px',
      contentPosition: 'center',
      enableParallax: false,
      enableTypingEffect: true,
    },
    schema: {
      type: 'object',
      properties: {
        backgroundType: {
          type: 'select',
          title: '背景类型',
          default: 'gradient',
          options: [
            { label: '渐变', value: 'gradient' },
            { label: '图片', value: 'image' },
            { label: '视频', value: 'video' },
          ],
        },
        backgroundUrl: {
          type: 'image',
          title: '背景图片/视频URL',
          description: '当背景类型为图片或视频时使用',
        },
        gradientColors: {
          type: 'array',
          title: '渐变颜色',
          items: {
            type: 'color',
            title: '颜色',
          },
        },
        title: {
          type: 'string',
          title: '标题',
          default: '欢迎来到我的博客',
        },
        subtitle: {
          type: 'string',
          title: '副标题',
          default: '分享技术，记录生活',
        },
        ctaText: {
          type: 'string',
          title: '按钮文字',
        },
        ctaLink: {
          type: 'string',
          title: '按钮链接',
        },
        height: {
          type: 'string',
          title: '高度',
          default: '500px',
        },
        contentPosition: {
          type: 'select',
          title: '内容位置',
          default: 'center',
          options: [
            { label: '居左', value: 'left' },
            { label: '居中', value: 'center' },
            { label: '居右', value: 'right' },
          ],
        },
        enableParallax: {
          type: 'boolean',
          title: '启用视差效果',
          default: false,
        },
        enableTypingEffect: {
          type: 'boolean',
          title: '启用打字机效果',
          default: true,
        },
      },
    },
  },

  ArticleGrid: {
    type: 'ArticleGrid',
    name: '文章网格',
    icon: 'Grid',
    description: '展示文章列表，支持多种布局',
    defaultProps: {
      columns: 3,
      cardStyle: 'default',
      showCover: true,
      showExcerpt: true,
      showTags: true,
      showDate: true,
      showReadCount: true,
      loadType: 'pagination',
      itemsPerPage: 9,
      hoverEffect: 'lift',
    },
    schema: {
      type: 'object',
      properties: {
        columns: {
          type: 'number',
          title: '列数',
          default: 3,
          min: 1,
          max: 4,
        },
        cardStyle: {
          type: 'select',
          title: '卡片样式',
          default: 'default',
          options: [
            { label: '默认', value: 'default' },
            { label: '简约', value: 'minimal' },
            { label: '杂志', value: 'magazine' },
          ],
        },
        showCover: {
          type: 'boolean',
          title: '显示封面',
          default: true,
        },
        showExcerpt: {
          type: 'boolean',
          title: '显示摘要',
          default: true,
        },
        showTags: {
          type: 'boolean',
          title: '显示标签',
          default: true,
        },
        showDate: {
          type: 'boolean',
          title: '显示日期',
          default: true,
        },
        showReadCount: {
          type: 'boolean',
          title: '显示阅读量',
          default: true,
        },
        loadType: {
          type: 'select',
          title: '加载方式',
          default: 'pagination',
          options: [
            { label: '分页', value: 'pagination' },
            { label: '无限滚动', value: 'infinite' },
          ],
        },
        itemsPerPage: {
          type: 'number',
          title: '每页数量',
          default: 9,
          min: 3,
          max: 20,
        },
        hoverEffect: {
          type: 'select',
          title: '悬停效果',
          default: 'lift',
          options: [
            { label: '上浮', value: 'lift' },
            { label: '缩放', value: 'zoom' },
            { label: '无', value: 'none' },
          ],
        },
      },
    },
  },

  CategoryNav: {
    type: 'CategoryNav',
    name: '分类导航',
    icon: 'Menu',
    description: '文章分类导航，支持多种样式',
    defaultProps: {
      style: 'tabs',
      showIcon: true,
      showCount: true,
      categories: [
        { id: 'all', name: '全部', icon: 'Grid', color: '#409EFF' },
        { id: 'tech', name: '技术', icon: 'Monitor', color: '#67C23A' },
        { id: 'life', name: '生活', icon: 'Coffee', color: '#E6A23C' },
        { id: 'travel', name: '旅行', icon: 'Location', color: '#F56C6C' },
      ],
    },
    schema: {
      type: 'object',
      properties: {
        style: {
          type: 'select',
          title: '导航样式',
          default: 'tabs',
          options: [
            { label: '标签页', value: 'tabs' },
            { label: '侧边栏', value: 'sidebar' },
            { label: '下拉菜单', value: 'dropdown' },
            { label: '药丸', value: 'pills' },
          ],
        },
        showIcon: {
          type: 'boolean',
          title: '显示图标',
          default: true,
        },
        showCount: {
          type: 'boolean',
          title: '显示数量',
          default: true,
        },
      },
    },
  },

  FriendLinks: {
    type: 'FriendLinks',
    name: '友情链接',
    icon: 'Link',
    description: '展示友情链接，支持多种布局',
    defaultProps: {
      displayMode: 'cards',
      columns: 3,
      showLogo: true,
      showDescription: true,
      hoverEffect: 'scale',
      links: [
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
      ],
    },
    schema: {
      type: 'object',
      properties: {
        displayMode: {
          type: 'select',
          title: '展示模式',
          default: 'cards',
          options: [
            { label: '卡片', value: 'cards' },
            { label: '列表', value: 'list' },
            { label: '轮播', value: 'carousel' },
          ],
        },
        columns: {
          type: 'number',
          title: '列数',
          default: 3,
          min: 2,
          max: 4,
        },
        showLogo: {
          type: 'boolean',
          title: '显示Logo',
          default: true,
        },
        showDescription: {
          type: 'boolean',
          title: '显示描述',
          default: true,
        },
        hoverEffect: {
          type: 'select',
          title: '悬停效果',
          default: 'scale',
          options: [
            { label: '缩放', value: 'scale' },
            { label: '发光', value: 'glow' },
            { label: '无', value: 'none' },
          ],
        },
      },
    },
  },

  InteractiveWidget: {
    type: 'InteractiveWidget',
    name: '互动组件',
    icon: 'ChatDotRound',
    description: '留言板、评论墙、访客地图',
    defaultProps: {
      widgetType: 'guestbook',
      theme: 'auto',
      enableEmoji: true,
      enableAvatar: true,
      maxItems: 10,
    },
    schema: {
      type: 'object',
      properties: {
        widgetType: {
          type: 'select',
          title: '组件类型',
          default: 'guestbook',
          options: [
            { label: '留言板', value: 'guestbook' },
            { label: '评论墙', value: 'comments' },
            { label: '访客地图', value: 'visitor-map' },
          ],
        },
        theme: {
          type: 'select',
          title: '主题',
          default: 'auto',
          options: [
            { label: '自动', value: 'auto' },
            { label: '浅色', value: 'light' },
            { label: '深色', value: 'dark' },
          ],
        },
        enableEmoji: {
          type: 'boolean',
          title: '启用表情',
          default: true,
        },
        enableAvatar: {
          type: 'boolean',
          title: '显示头像',
          default: true,
        },
        maxItems: {
          type: 'number',
          title: '最大显示数量',
          default: 10,
          min: 5,
          max: 50,
        },
      },
    },
  },
};

// 获取组件元数据
export function getComponentMeta(type: ComponentType): ComponentMeta {
  return componentRegistry[type];
}

// 获取所有组件列表
export function getAllComponents(): ComponentMeta[] {
  return Object.values(componentRegistry);
}

// 创建新组件实例
export function createComponent(type: ComponentType) {
  const meta = getComponentMeta(type);
  return {
    id: `${type}-${Date.now()}`,
    type,
    position: 0,
    visible: true,
    props: { ...meta.defaultProps },
    style: {
      backgroundColor: 'transparent',
      padding: '2rem',
      margin: '0',
      borderRadius: '0',
    },
  };
}

