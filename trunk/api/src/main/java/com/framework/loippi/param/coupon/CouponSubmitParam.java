package com.framework.loippi.param.coupon;

import lombok.Data;

/**
 * @author :ldq
 * @date:2019/10/22
 * @description:dubbo com.framework.loippi.param.coupon
 */
@Data
public class CouponSubmitParam {

	/**
	 * 优惠券id
	 */
	private Long couponId;

	/**
	 * 购买优惠券个数
	 * */
	private Integer couponNumber;

}
