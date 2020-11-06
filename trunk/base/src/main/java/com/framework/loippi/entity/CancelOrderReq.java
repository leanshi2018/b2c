package com.framework.loippi.entity;

import lombok.Data;

/**
 * 取消订单
 */
@Data
public class CancelOrderReq {

    // 客户订单号
    private String orderId;
}
