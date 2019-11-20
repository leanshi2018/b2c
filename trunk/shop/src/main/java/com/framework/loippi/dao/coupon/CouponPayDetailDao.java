package com.framework.loippi.dao.coupon;

import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.coupon.CouponPayDetail;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * @author :ldq
 * @date:2019/10/21
 * @description:dubbo com.framework.loippi.dao.coupon
 */
public interface CouponPayDetailDao extends GenericDao<CouponPayDetail, Long> {
	void insertCouponPay(CouponPayDetail couponPayDetail);

	List<CouponPayDetail> findByPaySn(String paysn);

	Long updateByIdAndOrderStateAndLockState(CouponPayDetail payDetail);

	void updateOrderAmout(CouponPayDetail couponPayDetail);

	void updateStateCouponPat(Map<String, Object> map);

	CouponPayDetail findBySn(Long couponOrderSn);
}
