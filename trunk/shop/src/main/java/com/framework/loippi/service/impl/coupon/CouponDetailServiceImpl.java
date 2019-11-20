package com.framework.loippi.service.impl.coupon;

import java.util.ArrayList;

import javax.annotation.Resource;

import com.framework.loippi.utils.Paramap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.dao.coupon.CouponDetailDao;
import com.framework.loippi.entity.coupon.CouponDetail;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.result.common.coupon.CouponUserLogResult;
import com.framework.loippi.service.coupon.CouponDetailService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;

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

    @Override
    public Object findLogResultByPage(Pageable pageable) {

        PageList<CouponUserLogResult> result = couponDetailDao.findLogResultByPage(pageable.getParameter(), pageable.getPageBounds());
        return new Page<>(result, result.getPaginator().getTotalCount(), pageable);
    }

    /**
     * 非金额支付优惠券过期下架回收
     * @param paramap
     */
    @Override
    public void recycleNoMoney(Paramap paramap) {
        couponDetailDao.recycleNoMoney(paramap);
    }
}
