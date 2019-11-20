package com.framework.loippi.service.impl.coupon;


import com.framework.loippi.dao.coupon.CouponUserDao;
import com.framework.loippi.entity.coupon.CouponUser;
import com.framework.loippi.service.coupon.CouponUserService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CouponUserServiceImpl extends GenericServiceImpl<CouponUser, Long> implements CouponUserService {
    @Resource
    private CouponUserDao couponUserDao;

    /**
     * 根据优惠券id回收CouponUser
     * @param couponId
     */
    @Override
    public void overdueCouponUserByCouponId(Long couponId) {
        couponUserDao.overdueCouponUserByCouponId(couponId);
    }
}
