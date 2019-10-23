package com.framework.loippi.service.coupon;

import com.framework.loippi.entity.coupon.CouponPayDetail;
import com.framework.loippi.entity.order.ShopOrderPay;
import com.framework.loippi.service.GenericService;

/**
 * @author :ldq
 * @date:2019/10/21
 * @description:dubbo com.framework.loippi.service.coupon
 */
public interface CouponPayDetailService extends GenericService<CouponPayDetail, Long> {
	ShopOrderPay addOrderReturnPaySn(String mmCode, Long couponId, Integer couponNumber);
}
