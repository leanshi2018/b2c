package com.framework.loippi.service.coupon;

import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.PayCommon;
import com.framework.loippi.entity.coupon.CouponPayDetail;
import com.framework.loippi.entity.order.ShopOrderPay;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.service.GenericService;

/**
 * @author :ldq
 * @date:2019/10/21
 * @description:dubbo com.framework.loippi.service.coupon
 */
public interface CouponPayDetailService extends GenericService<CouponPayDetail, Long> {
	ShopOrderPay addOrderReturnPaySn(String mmCode, Long couponId, Integer couponNumber);

	void ProcessingIntegralsCoupon(String paysn, Integer integration, RdMmBasicInfo shopMember, ShopOrderPay pay, Integer shoppingPointSr);

	void updateByPaySn(String paysn, Long paymentId);

	Map<String, Object> updateOrderpay(PayCommon payCommon, String memberId, String payName, String paymentCode,String paymentId);

	void updateCouponPayStateFinish(String paysn, String tradeSn, String paymentBranch);

	void updateStateCouponPat(Long id, int state);

	CouponPayDetail findBySn(String couponOrderSn);

	List<CouponPayDetail> findByPaySn(String PaySn);

	List<CouponPayDetail> findListByMCodeAndNotRefundStatus(String mmCode, int i);

	List<CouponPayDetail> findListByMCodeAndNotOrderStatus(String mmCode, int i);
}
