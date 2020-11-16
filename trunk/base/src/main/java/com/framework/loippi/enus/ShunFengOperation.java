package com.framework.loippi.enus;

/**
 * 顺丰操作类型
 *
 */
public enum ShunFengOperation {
    /** 顺丰操作类型 */
    CRETE_ORDER("下单"),
    CANCEL_ORDER("取消订单"),
    ROUTE_SEARCH("路由查询");

    ShunFengOperation(String chName) {
        this.chName = chName;
    }

    /**
     * 中文名称.
     */
    private String chName;

    /**
     * Gets the 中文名称.
     *
     * @return the 中文名称
     */
    public String getChName() {
        return chName;
    }
}
