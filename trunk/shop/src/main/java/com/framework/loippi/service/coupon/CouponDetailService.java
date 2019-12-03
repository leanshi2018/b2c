package com.framework.loippi.service.coupon;

import java.util.ArrayList;
import java.util.List;

import com.framework.loippi.entity.coupon.CouponDetail;
import com.framework.loippi.service.GenericService;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;

/**
 * 优惠券单体详情
 */
public interface CouponDetailService extends GenericService<CouponDetail, Long> {
    void updateList(ArrayList<CouponDetail> details);

	Object findLogResultByPage(Pageable pageable);

    void recycleNoMoney(Paramap paramap);

	List<CouponDetail> findListByBuyOrderId(Long id);
}
