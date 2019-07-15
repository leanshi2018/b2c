package com.framework.loippi.vo.order;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * 功能： 收款单excel超类
 * 类名：FinishOrderExcelVo
 * 日期：2017/9/11  9:34
 * 作者：czl
 * 详细说明：
 * 修改备注:
 */
@Data
public class FinishOrderExcelVo {

    /**
     * 支付单号
     */
    private String paySn;

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 会员号
     */
    private String buyerName;

    /**
     * 支付方式
     */
    private String paymentName;

    /**
     * 实付金额
     */
    private BigDecimal orderAmount;


    /**
     * 0:未付款 1:已付款
     */
    private String paymentState;

    /**
     * 支付时间
     */
    private Date payTime;

}
