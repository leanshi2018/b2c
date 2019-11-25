package com.framework.loippi.service.impl.coupon;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.framework.loippi.dao.coupon.CouponUserDao;
import com.framework.loippi.entity.coupon.CouponUser;
import com.framework.loippi.service.coupon.CouponUserService;
import com.framework.loippi.service.impl.GenericServiceImpl;

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

    @Override
    public List<CouponUser> findByMMCodeAndCouponId(String holdId, Long couponId) {
        Map<String,Object> map = new HashMap<>();
        map.put("mCode",holdId);
        map.put("couponId",couponId);
        return couponUserDao.findByMMCodeAndCouponId(map);
    }
}
