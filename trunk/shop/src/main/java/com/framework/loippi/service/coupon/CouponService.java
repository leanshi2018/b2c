package com.framework.loippi.service.coupon;

import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.service.GenericService;

import java.util.Map;

/**
 * 优惠券
 */
public interface CouponService extends GenericService<Coupon, Long> {
    Map<String, String> saveOrEditCoupon(Coupon coupon,Long id, String username);

    void updateCouponState(Coupon coupon, Integer targetStatus, com.framework.loippi.entity.Principal user) throws Exception;
}
