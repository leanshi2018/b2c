package com.framework.loippi.dao.coupon;

import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.coupon.CouponUser;
import com.framework.loippi.mybatis.dao.GenericDao;

public interface CouponUserDao extends GenericDao<CouponUser, Long> {
	List<CouponUser> findByMMCodeAndCouponId(Map<String, Object> map);
}
