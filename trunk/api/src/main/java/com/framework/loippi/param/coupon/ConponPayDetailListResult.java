package com.framework.loippi.param.coupon;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.coupon.CouponPayDetail;

/**
 * @author :ldq
 * @date:2019/11/14
 * @description:dubbo com.framework.loippi.param.coupon
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConponPayDetailListResult {

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
	 * 优惠券图片
	 */
	private String couponImage;
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
	 * 订单应付金额（最终现金支付金额）
	 * */
	private BigDecimal orderAmount;
	/**
	 * 订单状态：0:已取消;5待审核;10:待付款;40:交易完成;
	 * */
	private String OrderStateS;


	public static List<ConponPayDetailListResult> build(List<CouponPayDetail> couponPayDetailList, List<Coupon> coupons) {
		if (CollectionUtils.isEmpty(couponPayDetailList)) {
			return Collections.emptyList();
		}
		List<ConponPayDetailListResult> results = new ArrayList<ConponPayDetailListResult>();
		for (CouponPayDetail cpd : couponPayDetailList) {
			ConponPayDetailListResult result = new ConponPayDetailListResult();
			result.setId(cpd.getId());
			result.setCouponOrderSn(cpd.getCouponOrderSn());
			//result.setReceiveId(cpd.getReceiveId());
			//result.setReceiveNickName(cpd.getReceiveNickName());
			result.setCouponName(cpd.getCouponName());

			for (Coupon coupon : coupons) {
				if (coupon.getId().equals(cpd.getCouponId())){
					result.setCouponImage(coupon.getImage());
				}
			}
			result.setCreateTime(cpd.getCreateTime());
			result.setPaymentTime(cpd.getPaymentTime());
			result.setPaySn(cpd.getPaySn());
			result.setCouponAmount(cpd.getCouponAmount());
			result.setCouponNumber(cpd.getCouponNumber());
			result.setOrderAmount(cpd.getOrderAmount());
			if (cpd.getCouponOrderState()==40){
				result.setOrderStateS("交易完成");
			}else if (cpd.getCouponOrderState()==10){
				result.setOrderStateS("待付款");
			}else if (cpd.getCouponOrderState()==10){
				result.setOrderStateS("已取消");
			}
			results.add(result);
		}
		return results;
	}

}
