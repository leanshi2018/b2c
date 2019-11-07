package com.framework.loippi.service.coupon;

import com.framework.loippi.entity.coupon.CouponDetail;
import com.framework.loippi.service.GenericService;

import java.util.ArrayList;

/**
 * 优惠券单体详情
 */
public interface CouponDetailService extends GenericService<CouponDetail, Long> {
    void updateList(ArrayList<CouponDetail> details);
}
