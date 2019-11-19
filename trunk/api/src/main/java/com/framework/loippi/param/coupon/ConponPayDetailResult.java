package com.framework.loippi.param.coupon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.coupon.CouponDetail;
import com.framework.loippi.entity.coupon.CouponPayDetail;

/**
 * @author :ldq
 * @date:2019/11/15
 * @description:dubbo com.framework.loippi.param.coupon
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConponPayDetailResult {

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
	//private String receiveId;
	/**
	 * 领取人会员昵称(购买人)
	 */
	//private String receiveNickName;
	/**
	 * 优惠券名称
	 */
	private String couponName;
	/**
	 * 优惠券面值  针对于折扣卷为折扣大小
	 */
	private BigDecimal couponValue;
	/**
	 * 折扣类型  1：满减卷 2：立减卷 3：满金额折扣  4：无金额限制折扣
	 */
	private Integer reduceType;
	/**
	 * 优惠券图片
	 */
	private String couponImage;
	/**
	 * 是否可以赠送  0：不可以 1：可以
	 */
	private String whetherPresentS;
	/**
	 * 使用限制描述
	 */
	private String scopeRemark;
	/**
	 * 优惠券售价
	 */
	private BigDecimal couponPrice;
	/**
	 * 优惠券使用限期时间
	 */
	private String useDeadlineTime;
	/**
	 * 订单生成时间(下单时间)
	 * */
	private Date createTime;
	/**
	 * 付款状态:0:未付款;1:已付款
	 * */
	//private String paymentStateS;
	/**
	 * 支付(付款)时间
	 * */
	private Date paymentTime;
	/**
	 * 支付表编号
	 * */
	private String paySn;
	/**
	 * 优惠券总价格
	 * */
	private BigDecimal couponAmount;
	/**
	 * 优惠券个数
	 * */
	private Integer couponNumber;
	/**
	 * 订单所用积分数量
	 * */
	private BigDecimal usePointNum;
	/**
	 * 购物积分抵扣金额
	 * */
	private BigDecimal pointAmount;
	/**
	 * 订单应付金额（最终现金支付金额）
	 * */
	private BigDecimal orderAmount;
	/**
	 * 订单状态：已取消;已退款;待付款;交易完成;
	 * */
	private String OrderStateS;
	/**
	 * 订单状态：0:已取消;1:已退款;10:待付款;40:交易完成;
	 * */
	private Integer OrderState;
	/**
	 * 退款优惠券数量数量
	 * */
	private Integer refundCouponNum;
	/**
	 * 退款金额
	 * */
	private BigDecimal refundAmount;

	private List<CouponDetail> couponDetailList;


	public static ConponPayDetailResult build(CouponPayDetail couponPayDetail, Coupon coupon, List<CouponDetail> couponDetailList ) {

		ConponPayDetailResult result = new ConponPayDetailResult();

		result.setId(couponPayDetail.getId());
		result.setCouponOrderSn(couponPayDetail.getCouponOrderSn());
		//result.setReceiveId(cpd.getReceiveId());
		//result.setReceiveNickName(cpd.getReceiveNickName());
		result.setCouponName(coupon.getCouponName());
		result.setCouponValue(coupon.getCouponValue());
		result.setReduceType(coupon.getReduceType());
		result.setCouponImage(coupon.getImage());
		if (coupon.getWhetherPresent()==1){
			result.setWhetherPresentS("可赠送");
		}else if (coupon.getWhetherPresent()==0){
			result.setWhetherPresentS("不可赠送");
		}else {
			result.setWhetherPresentS("");
		}
		result.setScopeRemark(coupon.getScopeRemark());
		result.setCouponPrice(coupon.getCouponPrice());
		SimpleDateFormat form = new SimpleDateFormat("yyyy.MM.dd");
		String startTime = "";
		String endTime = "";
		if (coupon.getUseStartTime()!=null){
			startTime = form.format(coupon.getUseStartTime());
		}
		if (coupon.getUseEndTime()!=null){
			endTime = form.format(coupon.getUseEndTime());
		}else {
			endTime = "不限时";
		}
		result.setUseDeadlineTime(startTime+"-"+endTime);
		result.setCreateTime(couponPayDetail.getCreateTime());
		result.setPaymentTime(couponPayDetail.getPaymentTime());
		result.setPaySn(couponPayDetail.getPaySn());
		result.setCouponAmount(couponPayDetail.getCouponAmount());
		result.setCouponNumber(couponPayDetail.getCouponNumber());
		result.setUsePointNum(couponPayDetail.getUsePointNum());
		result.setPointAmount(couponPayDetail.getPointAmount());
		result.setOrderAmount(couponPayDetail.getOrderAmount());
		if (couponPayDetail.getCouponOrderState()==40){
			if (couponPayDetail.getRefundState()==1){
				result.setOrderStateS("已退款");
				result.setOrderState(couponPayDetail.getRefundState());
			}else{
				result.setOrderStateS("交易完成");
				result.setOrderState(couponPayDetail.getCouponOrderState());
			}
		}else if (couponPayDetail.getCouponOrderState()==10){
			result.setOrderStateS("待付款");
			result.setOrderState(couponPayDetail.getCouponOrderState());
		}else if (couponPayDetail.getCouponOrderState()==0){
			result.setOrderStateS("已取消");
			result.setOrderState(couponPayDetail.getCouponOrderState());
		}
		if (couponPayDetail.getRefundCouponNum()!=null){
			result.setRefundCouponNum(couponPayDetail.getRefundCouponNum());
		}
		if (couponPayDetail.getRefundAmount()!=null){
			result.setRefundAmount(couponPayDetail.getRefundAmount());
		}
		result.setCouponDetailList(couponDetailList);
		return result;
	}

}
