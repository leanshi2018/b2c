package com.framework.loippi.service.impl.coupon;

import com.framework.loippi.dao.coupon.CouponDetailDao;
import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.coupon.CouponDetail;
import com.framework.loippi.service.coupon.CouponDetailService;
import com.framework.loippi.service.coupon.CouponService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * 优惠券详情service层
 */
@Service
@Transactional
public class CouponDetailServiceImpl extends GenericServiceImpl<CouponDetail, Long> implements CouponDetailService {
    @Resource
    private  CouponDetailDao couponDetailDao;

    @Override
    public void updateList(ArrayList<CouponDetail> details) {
        couponDetailDao.updateList(details);
    }
}
