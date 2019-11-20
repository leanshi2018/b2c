package com.framework.loippi.service.coupon;

import com.framework.loippi.entity.coupon.CouponUser;
import com.framework.loippi.service.GenericService;

public interface CouponUserService extends GenericService<CouponUser, Long> {
    void overdueCouponUserByCouponId(Long id);
}
