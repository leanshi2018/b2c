package com.framework.loippi.service.impl.coupon;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.entity.coupon.CouponRefund;
import com.framework.loippi.service.coupon.CouponRefundService;
import com.framework.loippi.service.impl.GenericServiceImpl;

/**
 * @author :ldq
 * @date:2019/12/2
 * @description:dubbo com.framework.loippi.service.impl.coupon
 */
@Service
@Transactional
public class CouponRefundServiceImpl extends GenericServiceImpl<CouponRefund, Long> implements CouponRefundService {

}
