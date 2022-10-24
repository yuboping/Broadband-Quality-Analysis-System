package com.asiainfo.util.page;

import java.util.List;

/**
 *
 * @ClassName: Page
 * @Description: TODO(分页类)
 * @author yubp@asiainfo-sec.com
 * @date 2018年7月25日 上午10:28:49
 *
 * @param <T>
 */
public class Page {
    private int pageSize = 10; // 每页显示条数
    private int totalCount; // 总条数
    private int start; // 开始条数
    private int end; // 结束条数
    private int pageNumber;// 当前页
    private int totalPages; // 总页数
    private List<?> pageList;// 数据

    public Page(int totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * 获取下一条
     */
    public int getCurrentPageNumber() {
        return start / pageSize + 1;
    }

    /**
     * 是否有下一条
     *
     * @return
     */
    public boolean getHasNextPage() {
        return getCurrentPageNumber() < totalPages;
    }

    /**
     * 当前页是否大于1
     *
     * @return
     */
    public boolean getHasPavPage() {
        return getCurrentPageNumber() > 1;
    }

    /**
     * 获取总页数
     *
     * @return
     */
    public int getTotalPages() {
        totalPages = totalCount / pageSize;

        if (totalCount % pageSize != 0) {
            totalPages++;
        }

        return totalPages;
    }

    /**
     * 设置当前页的开始条数
     *
     * @param
     *
     * @return
     */
    public int getStart() {

        if (pageNumber < 1) {
            pageNumber = 1;
        } else if (getTotalPages() > 0 && pageNumber > getTotalPages()) {
            pageNumber = getTotalPages();
        }

        start = (pageNumber - 1) * pageSize + 1;
        return start;
    }

    /**
     * 设置当前页的结束条数
     *
     * @param
     *
     * @return
     */
    public int getEnd() {
        start = getStart();
        end = start + pageSize - 1;
        if (end > totalCount) {
            end = totalCount;
        }
        return end;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public List<?> getPageList() {
        return pageList;
    }

    public void setPageList(List<?> pageList) {
        this.pageList = pageList;
    }
}
