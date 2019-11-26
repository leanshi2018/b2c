package com.framework.loippi.dao.coupon;

import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.utils.Paramap;

import java.util.List;

public interface CouponDao extends GenericDao<Coupon, Long> {
    List<Coupon> findOverdueCoupon(Paramap put);

    Coupon judgeNoStart(Paramap put);

    Coupon judgeUseEnd(Paramap put);
}
