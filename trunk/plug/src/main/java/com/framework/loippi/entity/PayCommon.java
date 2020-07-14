package com.framework.loippi.entity;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author ihui
 *         支付公用参数
 */
@Data
@ToString
public class PayCommon implements Serializable {
    /**
     * 支付订单号
     */
    private String outTradeNo;
    /**
     * 服务器异步通知页面路径
     */
    private String notifyUrl;
    /**
     * 服务器同步通知页面路径
     */
    private String returnUrl;
    /**
     * 支付金额
     */
    private BigDecimal payAmount;
    /**
     * 支付点击返回跳转页面路径
     */
    private String backUrl;
    /**
     * 扩展字段
     */
    private String ext1;

    private String showUrl;

    private String title;

    private String body;

    private String openId;

    private Integer type;

}
