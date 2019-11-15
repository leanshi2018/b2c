package com.framework.loippi.dao.coupon;

import java.util.ArrayList;

import com.framework.loippi.entity.coupon.CouponDetail;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.mybatis.paginator.domain.PageBounds;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.result.common.coupon.CouponUserLogResult;

public interface CouponDetailDao extends GenericDao<CouponDetail, Long> {
    void updateList(ArrayList<CouponDetail> details);

	PageList<CouponUserLogResult> findLogResultByPage(Object parameter, PageBounds pageBounds);
}
