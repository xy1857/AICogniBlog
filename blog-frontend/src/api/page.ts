import axios from 'axios';
import type { PageConfig } from '@/types/page-builder';

const API_BASE = '/api/pages';

// 获取页面配置
export async function getPageConfig(id: string): Promise<PageConfig> {
  const response = await axios.get(`${API_BASE}/${id}`);
  return response.data;
}

// 获取用户的所有页面
export async function getUserPages(): Promise<PageConfig[]> {
  const response = await axios.get(`${API_BASE}/my`);
  return response.data;
}

// 创建页面
export async function createPage(config: Omit<PageConfig, 'id' | 'createdAt' | 'updatedAt'>): Promise<PageConfig> {
  const response = await axios.post(API_BASE, config);
  return response.data;
}

// 更新页面
export async function updatePage(id: string, config: Partial<PageConfig>): Promise<PageConfig> {
  const response = await axios.put(`${API_BASE}/${id}`, config);
  return response.data;
}

// 删除页面
export async function deletePage(id: string): Promise<void> {
  await axios.delete(`${API_BASE}/${id}`);
}

// 发布页面
export async function publishPage(id: string): Promise<void> {
  await axios.post(`${API_BASE}/${id}/publish`);
}

// 获取公开页面
export async function getPublicPage(username: string): Promise<PageConfig> {
  const response = await axios.get(`${API_BASE}/public/${username}`);
  return response.data;
}

