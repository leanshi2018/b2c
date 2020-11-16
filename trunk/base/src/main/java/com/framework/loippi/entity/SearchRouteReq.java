package com.framework.loippi.entity;

import lombok.Data;

/**
 * 路由查询请求ti
 */
@Data
public class SearchRouteReq {


    // 查询号类别:
    //1:根据顺丰运单号查询,trackingNumber将被当作顺丰运单号处理
    //2:根据客户订单号查询,trackingNumber将被当作客户订单号处理
    private String trackingType;
    // 查询号:
    //trackingType=1,则此值为顺丰运单号
    //如果trackingType=2,则此值为客户订单号
    private String trackingNumber;
}
