package com.framework.loippi.service.impl.coupon;

import com.framework.loippi.dao.coupon.CouponTransLogDao;
import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.coupon.CouponTransLog;
import com.framework.loippi.service.coupon.CouponService;
import com.framework.loippi.service.coupon.CouponTransLogService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * 优惠券日志表业务层
 */
@Service
@Transactional
public class CouponTransLogServiceImpl extends GenericServiceImpl<CouponTransLog, Long> implements CouponTransLogService {
    @Resource
    private  CouponTransLogDao couponTransLogDao;

    @Override
    public void insertList(ArrayList<CouponTransLog> logs) {
        couponTransLogDao.insertList(logs);
    }
}
