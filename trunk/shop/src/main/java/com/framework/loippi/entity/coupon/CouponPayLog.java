package com.framework.loippi.entity.coupon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * @author :ldq
 * @date:2019/10/23
 * @description:dubbo com.framework.loippi.entity.coupon
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_coupon_pay_log")
public class CouponPayLog implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/**
	 * 主键id索引
	 */
	private Long id;

	/** 优惠券支付订单id */
	private Long couponPayId;

	/** 订单状态信息 */
	private String orderState;

	/** 下一步订单状态信息 */
	private String changeState;

	/** 订单状态描述 */
	private String stateInfo;

	/** 处理时间 */
	private Date createTime;

	/** 操作人 */
	private String operator;
}
