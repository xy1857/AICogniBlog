package com.aicogniblog.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public class PageResult<T> {

    private long total;
    private long pages;
    private long current;
    private List<T> records;

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
