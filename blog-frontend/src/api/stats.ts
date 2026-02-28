import http from './http';

export interface StatsData {
  totalViews: number;
  todayViews: number;
  articleCount: number;
  commentCount: number;
  visitorCountries: number;
}

export const statsApi = {
  // 获取统计数据
  getStats: () => http.get<StatsData>('/stats'),
  
  // 获取访客统计
  getVisitorStats: () => http.get('/stats/visitor'),
};

