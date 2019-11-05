package com.framework.loippi.dao.coupon;

import com.framework.loippi.entity.coupon.CouponTransLog;
import com.framework.loippi.mybatis.dao.GenericDao;

import java.util.ArrayList;

/**
 * 优惠券交易日志
 */
public interface CouponTransLogDao extends GenericDao<CouponTransLog, Long> {
    void insertList(ArrayList<CouponTransLog> logs);
}
