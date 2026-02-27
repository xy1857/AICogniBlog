package com.aicogniblog.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 分页结果封装类
 * 
 * <p>用于封装分页查询的结果数据
 * 
 * @param <T> 数据类型
 * @author AICogniBlog Team
 * @since 1.0.0
 */
public class PageResult<T> {

    /** 总记录数 */
    private long total;
    
    /** 总页数 */
    private long pages;
    
    /** 当前页码 */
    private long current;
    
    /** 当前页数据列表 */
    private List<T> records;

    /**
     * 从 MyBatis-Plus 的 IPage 对象创建分页结果
     * 
     * @param page MyBatis-Plus 分页对象
     * @param <T> 数据类型
     * @return 分页结果
     */
    public static <T> PageResult<T> of(IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.total = page.getTotal();
        result.pages = page.getPages();
        result.current = page.getCurrent();
        result.records = page.getRecords();
        return result;
    }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public long getPages() { return pages; }
    public void setPages(long pages) { this.pages = pages; }

    public long getCurrent() { return current; }
    public void setCurrent(long current) { this.current = current; }

    public List<T> getRecords() { return records; }
    public void setRecords(List<T> records) { this.records = records; }
}
