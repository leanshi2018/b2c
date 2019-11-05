package com.framework.loippi.service.coupon;

import com.framework.loippi.entity.coupon.CouponTransLog;
import com.framework.loippi.service.GenericService;

import java.util.ArrayList;

/**
 * 优惠券交易日志
 */
public interface CouponTransLogService extends GenericService<CouponTransLog, Long> {
    void insertList(ArrayList<CouponTransLog> logs);
}
