package com.framework.loippi.service.impl.coupon;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.coupon.CouponDetailDao;
import com.framework.loippi.dao.coupon.CouponPayDetailDao;
import com.framework.loippi.dao.coupon.CouponUserDao;
import com.framework.loippi.dao.order.ShopOrderPayDao;
import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.coupon.CouponPayDetail;
import com.framework.loippi.entity.order.ShopOrderPay;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.coupon.CouponPayDetailService;
import com.framework.loippi.service.coupon.CouponService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.utils.Dateutil;

/**
 * @author :ldq
 * @date:2019/10/21
 * @description:dubbo com.framework.loippi.service.impl.order
 */
@Service
@Slf4j
public class CouponPayDetailServiceImpl  extends GenericServiceImpl<CouponPayDetail, Long> implements CouponPayDetailService {

	@Autowired
	private RdMmBasicInfoService rdMmBasicInfoService;
	@Autowired
	private TwiterIdService twiterIdService;
	@Autowired
	private ShopOrderPayDao orderPayDao;
	@Autowired
	private CouponService couponService;
	@Autowired
	private CouponPayDetailDao couponPayDetailDao;
	@Autowired
	private CouponDetailDao couponDetailDao;
	@Autowired
	private CouponUserDao couponUserDao;

	@Override
	public ShopOrderPay addOrderReturnPaySn(String memberId, Long couponId, Integer couponNumber) {

		//通过用户id查询用户信息
		RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode", memberId);
		//创建一个新的订单支付编号
		String paySn = "P" + Dateutil.getDateString();
		ShopOrderPay orderPay = new ShopOrderPay();
		orderPay.setId(twiterIdService.getTwiterId());
		orderPay.setPaySn(paySn);
		orderPay.setBuyerId(Long.parseLong(memberId));
		orderPay.setApiPayState("0");//设置支付状态0
		//保存订单支付表
		orderPayDao.insert(orderPay);

		//优惠券信息
		Coupon coupon = couponService.find("id", couponId);

		/*********************订单相关金额计算*********************/
		//单价
		BigDecimal couponPrice = coupon.getCouponPrice();
		//订单总价格
		BigDecimal orderTotal = couponPrice.multiply(new BigDecimal(couponNumber));


		return null;
	}
}
