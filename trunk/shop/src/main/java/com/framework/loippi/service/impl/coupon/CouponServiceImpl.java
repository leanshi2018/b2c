package com.framework.loippi.service.impl.coupon;

import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.service.coupon.CouponService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 优惠券业务层
 */
@Service
public class CouponServiceImpl extends GenericServiceImpl<Coupon, Long> implements CouponService {
}
