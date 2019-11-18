package com.framework.loippi.result.common.coupon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author :ldq
 * @date:2019/11/13
 * @description:dubbo com.framework.loippi.result.common.coupon
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponUserLogResult {

	private Long couponDetailId;//优惠券详情序号
	private Long couponId;//优惠券序号
	private String couponName;//优惠券名称
	private Integer reduceType;//折扣类型  1：满减卷 2：立减卷 3：满金额折扣  4：无金额限制折扣
	private Integer useScope;//使用范围 0：不限  1：适用于品类 2：适用于组合商品 3：使用于单品 4：适用于多种商品 9：组合条件使用
	private String receiveId;//领取人会员编号
	private String receiveNickName;//领取人昵称
	private String holdId;//持有人id （领取时默认领取人和持有人为同一人）
	private String holdNickName;//持有人昵称
	private Date receiveTime;//领取时间
	private Date useStartTime;//优惠券使用开始时间
	private Date useEndTime;//优惠券使用结束时间
	private Integer useState;//优惠券状态 1：已使用 2：未使用 3：已过期 4：已禁用
	private Long useOrderId;//使用优惠券关联订单id


}
