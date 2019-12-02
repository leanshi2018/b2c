package com.framework.loippi.entity.coupon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * @author :ldq
 * @date:2019/12/2
 * @description:dubbo com.framework.loippi.entity.coupon
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_coupon_refund")
public class CouponRefund implements GenericEntity {
	private static final long serialVersionUID = 5081846432919091193L;

	/**
	 * 主键id
	 */
	@Column(name = "id")
	private Long id;

	/**
	 * 退款表编号
	 */
	@Column(name = "refund_sn")
	private String refundSn;

	/**
	 * 关联优惠券支付订单id
	 */
	@Column(name = "pay_detail_id")
	private Long payDetailId;
	/**
	 * 退款数量
	 */
	@Column(name = "refund_num")
	private Integer refundNum;
	/**
	 * 优惠券id
	 */
	@Column(name = "coupon_id")
	private Long couponId;
	/**
	 * 退款金额数量
	 */
	@Column(name = "refund_amount")
	private BigDecimal refundAmount;
	/**
	 * 退款积分数量
	 */
	@Column(name = "refund_point")
	private BigDecimal refundPoint;
	/**
	 * 退款时间
	 */
	@Column(name = "refund_time")
	private Date refundTime;
	/**
	 * 购买人id
	 */
	@Column(name = "buyer_id")
	private String buyerId;
	/**
	 * 购买人姓名
	 */
	@Column(name = "buyer_name")
	private String buyerName;
	/**
	 * 订单支付外部编号
	 */
	@Column(name = "order_pay_sn")
	private String orderPaySn;
	/**
	 * 优惠券订单编号
	 */
	@Column(name = "order_sn")
	private String orderSn;
}
