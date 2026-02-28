import { defineStore } from 'pinia';
import type { PageConfig, ComponentConfig, ThemeConfig } from '@/types/page-builder';

interface PageBuilderState {
  currentPage: PageConfig | null;
  selectedComponentId: string | null;
  isDragging: boolean;
  history: PageConfig[];
  historyIndex: number;
}

export const usePageBuilderStore = defineStore('pageBuilder', {
  state: (): PageBuilderState => ({
    currentPage: null,
    selectedComponentId: null,
    isDragging: false,
    history: [],
    historyIndex: -1,
  }),

  getters: {
    selectedComponent(state): ComponentConfig | null {
      if (!state.currentPage || !state.selectedComponentId) return null;
      return state.currentPage.components.find(c => c.id === state.selectedComponentId) || null;
    },

    canUndo(state): boolean {
      return state.historyIndex > 0;
    },

    canRedo(state): boolean {
      return state.historyIndex < state.history.length - 1;
    },
  },

  actions: {
    // 初始化页面
    initPage(page?: PageConfig) {
      this.currentPage = page || {
        id: Date.now().toString(),
        name: '我的主页',
        theme: {
          primaryColor: '#409EFF',
          backgroundColor: '#f5f7fa',
          textColor: '#303133',
          fontFamily: 'Inter, system-ui, sans-serif',
          borderRadius: '8px',
        },
        components: [],
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      };
      this.saveHistory();
    },

    // 添加组件
    addComponent(component: ComponentConfig) {
      if (!this.currentPage) return;
      this.currentPage.components.push(component);
      this.currentPage.updatedAt = new Date().toISOString();
      this.saveHistory();
    },

    // 删除组件
    removeComponent(componentId: string) {
      if (!this.currentPage) return;
      this.currentPage.components = this.currentPage.components.filter(c => c.id !== componentId);
      if (this.selectedComponentId === componentId) {
        this.selectedComponentId = null;
      }
      this.currentPage.updatedAt = new Date().toISOString();
      this.saveHistory();
    },

    // 更新组件
    updateComponent(componentId: string, updates: Partial<ComponentConfig>) {
      if (!this.currentPage) return;
      const component = this.currentPage.components.find(c => c.id === componentId);
      if (component) {
        Object.assign(component, updates);
        this.currentPage.updatedAt = new Date().toISOString();
        this.saveHistory();
      }
    },

    // 更新组件属性
    updateComponentProps(componentId: string, props: Record<string, any>) {
      if (!this.currentPage) return;
      const component = this.currentPage.components.find(c => c.id === componentId);
      if (component) {
        component.props = { ...component.props, ...props };
        this.currentPage.updatedAt = new Date().toISOString();
        this.saveHistory();
      }
    },

    // 更新组件样式
    updateComponentStyle(componentId: string, style: Record<string, any>) {
      if (!this.currentPage) return;
      const component = this.currentPage.components.find(c => c.id === componentId);
      if (component) {
        component.style = { ...component.style, ...style };
        this.currentPage.updatedAt = new Date().toISOString();
        this.saveHistory();
      }
    },

    // 更新主题
    updateTheme(theme: Partial<ThemeConfig>) {
      if (!this.currentPage) return;
      this.currentPage.theme = { ...this.currentPage.theme, ...theme };
      this.currentPage.updatedAt = new Date().toISOString();
      this.saveHistory();
    },

    // 更新组件顺序
    updateComponentsOrder(components: ComponentConfig[]) {
      if (!this.currentPage) return;
      this.currentPage.components = components.map((c, index) => ({
        ...c,
        position: index,
      }));
      this.currentPage.updatedAt = new Date().toISOString();
      this.saveHistory();
    },

    // 选择组件
    selectComponent(componentId: string | null) {
      this.selectedComponentId = componentId;
    },

    // 保存历史记录
    saveHistory() {
      if (!this.currentPage) return;
      
      // 删除当前索引之后的历史
      this.history = this.history.slice(0, this.historyIndex + 1);
      
      // 添加新的历史记录
      this.history.push(JSON.parse(JSON.stringify(this.currentPage)));
      this.historyIndex++;
      
      // 限制历史记录数量
      if (this.history.length > 50) {
        this.history.shift();
        this.historyIndex--;
      }
    },

    // 撤销
    undo() {
      if (this.canUndo) {
        this.historyIndex--;
        this.currentPage = JSON.parse(JSON.stringify(this.history[this.historyIndex]));
      }
    },

    // 重做
    redo() {
      if (this.canRedo) {
        this.historyIndex++;
        this.currentPage = JSON.parse(JSON.stringify(this.history[this.historyIndex]));
      }
    },

    // 保存到本地存储
    saveToLocal() {
      if (!this.currentPage) return;
      localStorage.setItem('pageConfig', JSON.stringify(this.currentPage));
    },

    // 从本地存储加载
    loadFromLocal() {
      const saved = localStorage.getItem('pageConfig');
      if (saved) {
        try {
          this.currentPage = JSON.parse(saved);
          this.saveHistory();
        } catch (e) {
          console.error('Failed to load page config:', e);
        }
      }
    },
  },
});

