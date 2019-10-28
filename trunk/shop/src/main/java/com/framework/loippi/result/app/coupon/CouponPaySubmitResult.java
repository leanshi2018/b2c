package com.framework.loippi.result.app.coupon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import com.framework.loippi.entity.integration.RdMmIntegralRule;
import com.framework.loippi.entity.order.ShopOrderPay;
import com.framework.loippi.entity.user.RdMmAccountInfo;

/**
 * @author :ldq
 * @date:2019/10/24
 * @description:dubbo com.framework.loippi.result.app.coupon
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponPaySubmitResult {

	/**
	 * 支付订单编码
	 */
	private String couponOrderSn;
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

	public static CouponPaySubmitResult build(RdMmIntegralRule rdMmIntegralRule, ShopOrderPay orderPay, RdMmAccountInfo rdMmAccountInfo) {
		CouponPaySubmitResult couponPaySubmitResult = new CouponPaySubmitResult();
		couponPaySubmitResult.setCouponOrderSn(orderPay.getPaySn());
		couponPaySubmitResult.setIntegration(rdMmAccountInfo.getWalletBlance().setScale(2));
		couponPaySubmitResult.setOrderTotalPrice(orderPay.getPayAmount());
		if (rdMmIntegralRule==null || rdMmIntegralRule.getShoppingPointSr()==null){
			couponPaySubmitResult.setProportion(0d);
		}else{
			couponPaySubmitResult.setProportion(rdMmIntegralRule.getShoppingPointSr().doubleValue()*0.01);
		}
		return couponPaySubmitResult;
	}

}
