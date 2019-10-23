package com.framework.loippi.entity.coupon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * @author :ldq
 * @date:2019/10/21
 * @description:dubbo com.framework.loippi.entity.coupon
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_coupon_pay_detail")
public class CouponPayDetail implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/**
	 * 主键id索引
	 */
	private Long id;
	/**
	 * 优惠券订单编号
	 */
	private String couponOrderSn;
	/**
	 * 领取人会员编号(购买人)
	 */
	private String receiveId;
	/**
	 * 领取人会员昵称(购买人)
	 */
	private String receiveNickName;
	/**
	 * 优惠券id
	 */
	private Long couponId;
	/**
	 * 优惠券名称
	 */
	private String couponName;
	/**
	 * 订单生成时间(下单时间)
	 * */
	private Date createTime;
	/**
	 * 支付方式id
	 * */
	private Long paymentId;
	/**
	 * 支付方式名称
	 * */
	private String paymentName;
	/**
	 * 付款状态:0:未付款;1:已付款
	 * */
	private Integer paymentState;
	/**
	 * 支付(付款)时间
	 * */
	private Date paymentTime;
	/**
	 * 优惠券总价格
	 * */
	private BigDecimal couponAmount;
	/**
	 * 优惠券个数
	 * */
	private Integer couponNumber;
	/**
	 * 购物积分抵扣金额
	 * */
	private BigDecimal pointAmount;
	/**
	 * 退款状态:0是无退款,1是部分退款,2是全部退款
	 * */
	private Integer refundState;
	/**
	 * 退款优惠券数量数量
	 * */
	private Integer refundCouponNum;
	/**
	 * 退款金额
	 * */
	private BigDecimal refundAmount;
	/**
	 * 退款时间
	 */
	private Date refundTime;
	/**
	 *订单取消时间，默认为null
	 */
	private Date cancelTime;
	/**
	 *操作时间（更新时间）
	 */
	private Date updateTime;



}