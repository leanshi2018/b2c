package com.framework.loippi.result.app.order;

import com.framework.loippi.entity.integration.RdMmIntegralRule;
import com.framework.loippi.entity.order.ShopOrderPay;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 换购订单提交返回app数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedemptionOrderSubmitResult {
    /**
     * 订单编号
     */
    private Long orderId;

    /**
     * 支付订单编码
     */
    private String orderPaySn;
    /**
     * 需要支付订单的总价格
     */
    private BigDecimal orderTotalPrice;

    /**
     * 用户换购积分
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

    /**
     * 0默认未支付1已支付(只有第三方支付接口通知到时才会更改此状态)
     */
    private String apiPayState;

    public static RedemptionOrderSubmitResult build(RdMmIntegralRule rdMmIntegralRule, ShopOrderPay orderPay, RdMmAccountInfo rdMmAccountInfo) {
        RedemptionOrderSubmitResult result = new RedemptionOrderSubmitResult();
        result.setOrderId(orderPay.getOrderId());
        result.setOrderPaySn(orderPay.getPaySn());
        result.setIntegration(rdMmAccountInfo.getRedemptionBlance().setScale(2));
        result.setOrderTotalPrice(orderPay.getPayAmount());
        if (rdMmIntegralRule==null || rdMmIntegralRule.getShoppingPointSr()==null){
            result.setProportion(0d);
        }else{
            result.setProportion(rdMmIntegralRule.getShoppingPointSr().doubleValue()*0.01);
        }
        result.setPaymentType(Optional.ofNullable(orderPay.getPaymentType()).orElse(1));
        result.setApiPayState(orderPay.getApiPayState());
        return result;
    }
}
