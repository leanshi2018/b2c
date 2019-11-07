package com.framework.loippi.service.coupon;

import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.coupon.CouponUser;
import com.framework.loippi.service.GenericService;
import com.framework.loippi.utils.Paramap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 优惠券
 */
public interface CouponService extends GenericService<Coupon, Long> {
    Map<String, String> saveOrEditCoupon(Coupon coupon,Long id, String username);

    HashMap<String, Object> transactionCoupon(String mmCode,String mNickName, String recipientCode, Coupon coupon, CouponUser couponUser, Integer transNum)  throws Exception;

    void updateCouponState(Coupon coupon, Integer targetStatus, Long id,String username) throws Exception;

    List<Coupon> findOverdueCoupon(Paramap put);
}
