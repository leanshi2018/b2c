package com.framework.loippi.result.app.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Optional;

import com.framework.loippi.entity.integration.RdMmIntegralRule;
import com.framework.loippi.entity.order.ShopOrderPay;
import com.framework.loippi.entity.user.RdMmAccountInfo;

/**
 * 提交订单-返回app数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor

public class OrderSubmitResult {

    /**
     * 支付订单编码
     */
    private String orderPaySn;
    /**
     * 需要支付订单的总价格
     */
    private BigDecimal orderTotalPrice;

    /**
     * 用户购物积分
     */
    private BigDecimal integration;
    /**
     * 比例
     */
    private Double proportion;
    /**
     * 1在线支付 2货到付款
     */
    private Integer paymentType;

    //订单是否使用过积分
    private Boolean usePointFlag;

    public static OrderSubmitResult build(RdMmIntegralRule rdMmIntegralRule,ShopOrderPay orderPay, RdMmAccountInfo rdMmAccountInfo) {
        OrderSubmitResult orderSubmitResult = new OrderSubmitResult();
        orderSubmitResult.setOrderPaySn(orderPay.getPaySn());
        orderSubmitResult.setIntegration(rdMmAccountInfo.getWalletBlance().setScale(2));
        orderSubmitResult.setOrderTotalPrice(orderPay.getPayAmount());
        if (rdMmIntegralRule==null || rdMmIntegralRule.getShoppingPointSr()==null){
            orderSubmitResult.setProportion(0d);
        }else{
            orderSubmitResult.setProportion(rdMmIntegralRule.getShoppingPointSr().doubleValue()*0.01);
        }
        orderSubmitResult.setPaymentType(Optional.ofNullable(orderPay.getPaymentType()).orElse(1));
        orderSubmitResult.setUsePointFlag(orderPay.getUsePointFlag());
        return orderSubmitResult;
    }

}
