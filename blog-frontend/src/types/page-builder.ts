// 页面装修系统类型定义

export type ComponentType = 
  | 'HeroBanner' 
  | 'ArticleGrid' 
  | 'CategoryNav' 
  | 'FriendLinks' 
  | 'InteractiveWidget';

// 主题配置
export interface ThemeConfig {
  primaryColor: string;
  backgroundColor: string;
  textColor: string;
  fontFamily: string;
  borderRadius: string;
}

// 组件配置基类
export interface ComponentConfig {
  id: string;
  type: ComponentType;
  position: number;
  visible: boolean;
  props: Record<string, any>;
  style: {
    backgroundColor?: string;
    padding?: string;
    margin?: string;
    borderRadius?: string;
    [key: string]: any;
  };
}

// Hero横幅配置
export interface HeroBannerProps {
  backgroundType: 'image' | 'video' | 'gradient';
  backgroundUrl?: string;
  gradientColors?: string[];
  title: string;
  subtitle: string;
  ctaText?: string;
  ctaLink?: string;
  height: string;
  contentPosition: 'left' | 'center' | 'right';
  enableParallax: boolean;
  enableTypingEffect: boolean;
}

// 文章网格配置
export interface ArticleGridProps {
  columns: number;
  cardStyle: 'default' | 'minimal' | 'magazine';
  showCover: boolean;
  showExcerpt: boolean;
  showTags: boolean;
  showDate: boolean;
  showReadCount: boolean;
  loadType: 'pagination' | 'infinite';
  itemsPerPage: number;
  hoverEffect: 'lift' | 'zoom' | 'none';
}

// 分类导航配置
export interface CategoryNavProps {
  style: 'tabs' | 'sidebar' | 'dropdown' | 'pills';
  showIcon: boolean;
  showCount: boolean;
  categories: Array<{
    id: string;
    name: string;
    icon?: string;
    color?: string;
  }>;
}

// 友情链接配置
export interface FriendLinksProps {
  displayMode: 'cards' | 'list' | 'carousel';
  columns: number;
  showLogo: boolean;
  showDescription: boolean;
  links: Array<{
    id: string;
    name: string;
    url: string;
    logo?: string;
    description?: string;
  }>;
  hoverEffect: 'scale' | 'glow' | 'none';
}

// 互动组件配置
export interface InteractiveWidgetProps {
  widgetType: 'guestbook' | 'comments' | 'visitor-map';
  theme: 'light' | 'dark' | 'auto';
  enableEmoji: boolean;
  enableAvatar: boolean;
  maxItems: number;
}

// 页面配置
export interface PageConfig {
  id: string;
  name: string;
  theme: ThemeConfig;
  components: ComponentConfig[];
  createdAt: string;
  updatedAt: string;
}

// 组件元数据
export interface ComponentMeta {
  type: ComponentType;
  name: string;
  icon: string;
  description: string;
  defaultProps: Record<string, any>;
  schema: FormSchema;
}

// 表单Schema定义
export interface FormSchema {
  type: 'object';
  properties: Record<string, FormField>;
}

export interface FormField {
  type: 'string' | 'number' | 'boolean' | 'array' | 'object' | 'color' | 'image' | 'select' | 'textarea';
  title: string;
  default?: any;
  options?: Array<{ label: string; value: any }>;
  min?: number;
  max?: number;
  description?: string;
  properties?: Record<string, FormField>;
  items?: FormField;
}

