package com.framework.loippi.service.impl.coupon;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.consts.OrderState;
import com.framework.loippi.controller.AppConstants;
import com.framework.loippi.controller.StateResult;
import com.framework.loippi.dao.coupon.CouponDetailDao;
import com.framework.loippi.dao.coupon.CouponPayDetailDao;
import com.framework.loippi.dao.coupon.CouponPayLogDao;
import com.framework.loippi.dao.coupon.CouponUserDao;
import com.framework.loippi.dao.order.ShopOrderPayDao;
import com.framework.loippi.dao.user.RdSysPeriodDao;
import com.framework.loippi.entity.PayCommon;
import com.framework.loippi.entity.TSystemPluginConfig;
import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.coupon.CouponDetail;
import com.framework.loippi.entity.coupon.CouponPayDetail;
import com.framework.loippi.entity.coupon.CouponPayLog;
import com.framework.loippi.entity.coupon.CouponUser;
import com.framework.loippi.entity.order.ShopOrderPay;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.service.TSystemPluginConfigService;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.coupon.CouponPayDetailService;
import com.framework.loippi.service.coupon.CouponService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdMmAccountInfoService;
import com.framework.loippi.service.user.RdMmAccountLogService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.utils.Dateutil;
import com.framework.loippi.utils.SnowFlake;
import com.google.common.collect.Maps;

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
	@Autowired
	private CouponPayLogDao couponPayLogDao;
	@Resource
	private RdMmAccountInfoService rdMmAccountInfoService;
	@Resource
	private RdMmAccountLogService rdMmAccountLogService;
	@Resource
	private RdSysPeriodDao rdSysPeriodDao;
	@Autowired
	private TSystemPluginConfigService tSystemPluginConfigService;
	@Autowired
	private CouponUserDao couponUserDao;

	@Override
	public ShopOrderPay addOrderReturnPaySn(String memberId, Long couponId, Integer couponNumber) {

		//通过用户id查询用户信息
		RdMmBasicInfo member = rdMmBasicInfoService.find("mmCode", memberId);
		//创建一个新的订单支付编号
		String paySn = "Y" + Dateutil.getDateString();
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

		/***************************订单保存*****************************/
		//优惠券订单表
		CouponPayDetail couponPayDetail = new CouponPayDetail();
		couponPayDetail.setId(twiterIdService.getTwiterId());
		SnowFlake snowFlake = new SnowFlake(0, 0);
		couponPayDetail.setCouponOrderSn("AY" + snowFlake.nextId());
		couponPayDetail.setReceiveId(memberId);
		couponPayDetail.setReceiveNickName(member.getMmNickName());
		couponPayDetail.setCouponId(couponId);
		couponPayDetail.setCouponName(coupon.getCouponName());
		couponPayDetail.setCreateTime(new Date());
		//没有支付id 暂时为0L
		couponPayDetail.setPaymentId(0l);
		couponPayDetail.setPaymentName("");
		couponPayDetail.setPayId(orderPay.getId());
		couponPayDetail.setPaySn(paySn);
		if (orderTotal.compareTo(new BigDecimal("0"))==0){//订单总价格为0，视为已经支付订单
			couponPayDetail.setPaymentState(1);
		}else if (orderTotal.compareTo(new BigDecimal("0"))==1){//订单总价格大于0
			couponPayDetail.setPaymentState(0);
		}else {
			throw new IllegalStateException("创建订单平台错误");
		}
		couponPayDetail.setCouponAmount(orderTotal);
		couponPayDetail.setCouponNumber(couponNumber);
		couponPayDetail.setOrderAmount(orderTotal);
		couponPayDetail.setUpdateTime(new Date());
		couponPayDetailDao.insertCouponPay(couponPayDetail);

		/*********************保存日志*********************/
		CouponPayLog payLog = new CouponPayLog();
		payLog.setId(twiterIdService.getTwiterId());
		payLog.setOperator(member.getMmCode().toString());
		payLog.setChangeState(1 + "");//下一步 已付款或者取消
		payLog.setCouponPayId(couponPayDetail.getId());
		payLog.setOrderState(0 + "");
		payLog.setStateInfo("提交订单");
		payLog.setCreateTime(new Date());
		couponPayLogDao.insert(payLog);

		//根据payId查询订单列表
		orderPay.setOrderCreateTime(new Date());
		orderPay.setPayAmount(orderTotal);
		orderPay.setOrderTotalPrice(orderTotal);
		orderPay.setOrderId(couponPayDetail.getId());
		orderPay.setOrderSn(couponPayDetail.getCouponOrderSn());

		return orderPay;
	}

	@Override
	public void ProcessingIntegralsCoupon(String paysn, Integer integration, RdMmBasicInfo shopMember, ShopOrderPay pay,
										  Integer shoppingPointSr) {//购物积分购物比例
		//第一步 判断积分是否正确
		if (integration < 0) {
			throw new StateResult(AppConstants.GOODS_STATE_ERRO, "要使用的积分不能小于0");
		}
		//积分
		RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", shopMember.getMmCode());

		if (rdMmAccountInfo == null) {
			throw new StateResult(AppConstants.GOODS_STATE_ERRO, "用户积分不正确");
		}
		if (rdMmAccountInfo.getWalletStatus() != 0) {
			throw new StateResult(AppConstants.GOODS_STATE_ERRO, "用户积分未激活或者已冻结 ");
		}

		if (rdMmAccountInfo.getWalletBlance().compareTo(BigDecimal.valueOf(integration)) == -1) {
			throw new StateResult(AppConstants.GOODS_STATE_ERRO, "要使用的积分不能大于拥有积分");
		}


		BigDecimal shoppingPoints = new BigDecimal(integration * shoppingPointSr * 0.01);
		if (shoppingPoints.compareTo(new BigDecimal("0")) == 1){//使用抵现积分大于0
			if (shoppingPoints.compareTo(pay.getPayAmount()) == 1) {
				throw new StateResult(AppConstants.GOODS_STATE_ERRO, "要抵现的不能大于订单金额");
			}
			if (shoppingPoints.compareTo(pay.getPayAmount()) == -1) {
				throw new StateResult(AppConstants.GOODS_STATE_ERRO, "要抵现的不能小于订单金额");
			}
		}

		//修改订单价格
		List<CouponPayDetail> payDetailList = couponPayDetailDao.findByPaySn(paysn);
		Long payDetailId = 0L;
		if (payDetailList != null && payDetailList.size() > 0) {
			for (CouponPayDetail couponPayDetail : payDetailList) {
				if (couponPayDetail.getPaymentState() == 1) {
					throw new StateResult(AppConstants.GOODS_STATE_ERRO, "订单已支付");
				}
				payDetailId = couponPayDetail.getId();
				int pointNum = 0;
				pointNum = new BigDecimal(
						(couponPayDetail.getCouponAmount().doubleValue() / pay.getPayAmount().doubleValue()) * (integration))
						.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
				couponPayDetail.setUsePointNum(Optional.ofNullable(couponPayDetail.getUsePointNum()).orElse(new BigDecimal(0.00)).add(new BigDecimal(pointNum)));//设置订单所用积分数量
				couponPayDetail.setPointAmount(Optional.ofNullable(couponPayDetail.getPointAmount()).orElse(BigDecimal.ZERO)
						.add(new BigDecimal(pointNum * shoppingPointSr * 0.01).setScale(2, BigDecimal.ROUND_HALF_UP)));
				couponPayDetail.setOrderAmount(couponPayDetail.getOrderAmount()
						.subtract(new BigDecimal(pointNum * shoppingPointSr * 0.01).setScale(2, BigDecimal.ROUND_HALF_UP)));
				couponPayDetailDao.update(couponPayDetail);
			}
		} else {
			throw new StateResult(AppConstants.GOODS_STATE_ERRO, "订单不存在");
		}
		//更新用户购物积分
		RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
		rdMmAccountLog.setTransTypeCode("OP");
		rdMmAccountLog.setAccType("");
		rdMmAccountLog.setTrSourceType("SWB");
		rdMmAccountLog.setMmCode(shopMember.getMmCode());
		rdMmAccountLog.setMmNickName(shopMember.getMmNickName());
		rdMmAccountLog.setTrMmCode(shopMember.getMmCode());
		rdMmAccountLog.setBlanceBefore(rdMmAccountInfo.getWalletBlance());
		rdMmAccountLog.setAmount(BigDecimal.valueOf(integration));
		rdMmAccountLog.setTransDate(new Date());
		String period = rdSysPeriodDao.getSysPeriodService(new Date());
		rdMmAccountLog.setTransPeriod(period);
		rdMmAccountLog.setTrOrderOid(payDetailId);
		//无需审核直接成功
		rdMmAccountLog.setStatus(3);
		rdMmAccountLog.setCreationBy(shopMember.getMmNickName());
		rdMmAccountLog.setCreationTime(new Date());
		rdMmAccountLog.setAutohrizeBy(shopMember.getMmNickName());
		rdMmAccountLog.setAutohrizeTime(new Date());
		rdMmAccountInfo.setWalletBlance(rdMmAccountInfo.getWalletBlance().subtract(BigDecimal.valueOf(integration)));
		rdMmAccountLog.setBlanceAfter(rdMmAccountInfo.getWalletBlance());
		rdMmAccountInfoService.update(rdMmAccountInfo);
		rdMmAccountLogService.save(rdMmAccountLog);
	}

	@Override
	public void updateByPaySn(String paysn, Long paymentId) {
		CouponPayDetail payDetail = find("paySn", paysn);
		TSystemPluginConfig payment = tSystemPluginConfigService.find(paymentId);
		// 更新
		//payDetail.setPaymentCode(payment.getPluginId()); //支付方式名称代码
		payDetail.setPaymentId(payment.getId()); //支付方式id
		payDetail.setPaymentName(payDetail.getPaymentName() + payment.getPluginName()); //支付方式名称
		payDetail.setPrevOrderState(0);//锁定支付前的支付状态
		updateByIdOrderStateLockState(payDetail, OrderState.ORDER_OPERATE_PAY);
	}

	public void updateByIdOrderStateLockState(CouponPayDetail payDetail, int operateType) {
		if (payDetail.getId() == null) {
			throw new IllegalArgumentException("参数错误");
		}

		if (operateType != OrderState.ORDER_OPERATE_PAY ) {
			throw new IllegalArgumentException("参数错误");
		}

		Long result = couponPayDetailDao.updateByIdAndOrderStateAndLockState(payDetail);
		if (result.intValue() != 1) {
			CouponPayDetail findpayDetail = couponPayDetailDao.find(payDetail.getId());
			if (findpayDetail == null) {
				throw new RuntimeException("不存在订单");
			}

			String exceptionMsg = null;
			switch (operateType) {
				case OrderState.ORDER_OPERATE_PAY:
					// 【前端订单发起支付中】 -- 【后台取消订单中】 => 后台取消订单先完成
					if (!findpayDetail.getPaymentState().equals(payDetail.getPrevOrderState())) {
						exceptionMsg = "订单已经支付";
					}
					// 【后台订单编辑中】 -- 【前端订单发起支付中】 => 后台先完成
					/*if (!findpayDetail.getLockState().equals(payDetail.getPrevLockState())) {
						exceptionMsg = "订单编辑中, 请重新支付";
					}*/
					break;
				case OrderState.ORDER_OPERATE_CANCEL:
					// 【前端订单支付中锁定订单】 -- 【后台取消订单中】 =》订单支付中先锁定
					/*if (!findOrder.getLockState().equals(order.getPrevLockState())) {
						exceptionMsg = "订单已经锁定";
					}*/
					// 【订单支付第三方调用完成支付接口】-- 【后台在取消订单中】 =》第三方调用先完成
					if (!findpayDetail.getPaymentState().equals(payDetail.getPrevOrderState())) {
						exceptionMsg = "订单已经支付成功";
					}
					break;
				default:
					exceptionMsg = "订单更新失败";
					break;
			}

			throw new StateResult(AppConstants.ORDER_UPDATE_FAIL, exceptionMsg);
		}
	}

	@Override
	public Map<String, Object> updateOrderpay(PayCommon payCommon, String memberId, String payName, String paymentCode, String paymentId) {

		RdMmBasicInfo shopMember = rdMmBasicInfoService.find("mmCode", memberId);

		List<CouponPayDetail> couponPayList = findList("paySn", payCommon.getOutTradeNo());
		String couponOrderSn = "";
		if (CollectionUtils.isNotEmpty(couponPayList)) {
			for (CouponPayDetail couponPayDetail : couponPayList) {
				if (couponPayDetail.getPaymentState() == 1) {
					throw new StateResult(AppConstants.GOODS_STATE_ERRO, "订单已支付");
				}
				if (couponPayDetail.getPaymentState() == 0) {
					couponOrderSn += couponPayDetail.getCouponOrderSn() + ",";
					//新建一个订单日志
					CouponPayLog couponPayLog = new CouponPayLog();
					couponPayLog.setId(twiterIdService.getTwiterId());
					couponPayLog.setOrderState(1 + "");
					couponPayLog.setChangeState(1 + "");
					couponPayLog.setStateInfo("订单付款完成");
					couponPayLog.setCouponPayId(couponPayDetail.getId());
					couponPayLog.setOperator(shopMember.getMmCode());
					couponPayLog.setCreateTime(new Date());
					//保存订单日志
					couponPayLogDao.insert(couponPayLog);
					//修改订单状态
					CouponPayDetail newcouponPay = new CouponPayDetail();
					newcouponPay.setPaymentState(OrderState.PAYMENT_STATE_YES);
					newcouponPay.setPaymentTime(new Date());
					couponPayDetailDao.update(newcouponPay);
					//todo 暂时
					newcouponPay.setPaymentName(payName);
					newcouponPay.setPaymentId(Long.valueOf(paymentId));
					// 条件
					newcouponPay.setId(couponPayDetail.getId());
					newcouponPay.setPrevOrderState(0);
					updateByIdOrderStateLockState(newcouponPay, OrderState.ORDER_OPERATE_PAY);
				}
				//换购订单 扣除换购积分 并生成记录
				/*if ("10".equals(paymentId)) {
					//设置换购积分消息通知
					ShopCommonMessage shopCommonMessage=new ShopCommonMessage();
					shopCommonMessage.setSendUid(memberId);
					shopCommonMessage.setType(1);
					shopCommonMessage.setOnLine(1);
					shopCommonMessage.setCreateTime(new Date());
					shopCommonMessage.setBizType(2);
					shopCommonMessage.setIsTop(1);
					shopCommonMessage.setCreateTime(new Date());
					shopCommonMessage.setTitle("积分扣减通知");
					shopCommonMessage.setContent("您因支付换购订单："+order.getOrderSn()+"订单扣减"+order.getOrderAmount()+"点积分，请在换购积分账户查看明细");
					Long msgId1 = twiterIdService.getTwiterId();
					shopCommonMessage.setId(msgId1);
					shopCommonMessageDao.insert(shopCommonMessage);
					ShopMemberMessage shopMemberMessage1=new ShopMemberMessage();
					shopMemberMessage1.setBizType(2);
					shopMemberMessage1.setCreateTime(new Date());
					shopMemberMessage1.setId(twiterIdService.getTwiterId());
					shopMemberMessage1.setIsRead(0);
					shopMemberMessage1.setMsgId(msgId1);
					shopMemberMessage1.setUid(Long.parseLong(memberId));
					shopMemberMessageDao.insert(shopMemberMessage1);
					RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", shopMember.getMmCode());
					RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
					rdMmAccountLog.setTransTypeCode("EG");
					rdMmAccountLog.setAccType("");
					rdMmAccountLog.setTrSourceType("CMP");
					rdMmAccountLog.setMmCode(shopMember.getMmCode());
					rdMmAccountLog.setMmNickName(shopMember.getMmNickName());
					rdMmAccountLog.setTrMmCode(shopMember.getMmCode());
					rdMmAccountLog.setBlanceBefore(rdMmAccountInfo.getRedemptionBlance());
					rdMmAccountLog.setAmount(order.getOrderAmount());
					rdMmAccountLog.setTrOrderOid(order.getId());
					rdMmAccountLog.setTransDate(new Date());
					String period = rdSysPeriodDao.getSysPeriodService(new Date());
					rdMmAccountLog.setTransPeriod(period);
					//提现需审核初始为申请状态
					rdMmAccountLog.setStatus(3);
					rdMmAccountLog.setCreationBy(shopMember.getMmNickName());
					rdMmAccountLog.setCreationTime(new Date());
					rdMmAccountInfo
							.setRedemptionBlance(rdMmAccountInfo.getRedemptionBlance().subtract(order.getOrderAmount()));
					rdMmAccountLog.setBlanceAfter(rdMmAccountInfo.getRedemptionBlance());
					List<RdMmAccountLog> rdMmAccountLogList = new ArrayList<>();
					rdMmAccountLogList.add(rdMmAccountLog);
					Integer transNumber = rdMmAccountInfoService
							.saveAccountInfo(rdMmAccountInfo, order.getOrderAmount().intValue(), IntegrationNameConsts.PUI,
									rdMmAccountLogList, null);
				}*/

//                // 用户增加消费积分
//                addMemberConsumePoints(payCommon.getPayAmount().doubleValue(), shopMember.getId());
			}
			Map<String, Object> result = Maps.newConcurrentMap();
			result.put("status", 1);
			result.put("couponOrderSn", couponOrderSn);
			result.put("message", "支付成功");

			//生成优惠券用户表数据
			Map<String, Object> couponUserMap = new HashMap<>();
			couponUserMap.put("mCode",memberId);
			couponUserMap.put("couponId",couponPayList.get(0).getCouponId());
			List<CouponUser> couponUserList = couponUserDao.findByMMCodeAndCouponId(couponUserMap);
			if (couponUserList.size()==0){
				CouponUser couponUserNew = new CouponUser();
				couponUserNew.setId(twiterIdService.getTwiterId());
				couponUserNew.setMCode(memberId);
				couponUserNew.setCouponId(couponPayList.get(0).getCouponId());
				couponUserNew.setOwnNum(couponPayList.get(0).getCouponNumber());
				couponUserNew.setUseAbleNum(1);
				couponUserNew.setUseNum(0);
				couponUserDao.insert(couponUserNew);
			}else {
				for (CouponUser couponUser : couponUserList) {
					Integer ownNum = couponUser.getOwnNum();
					couponUser.setOwnNum(ownNum+couponPayList.get(0).getCouponNumber());
					couponUserDao.update(couponUser);
				}
			}

			//生成优惠券详情表



			return result;

		}
		Map<String, Object> result = Maps.newConcurrentMap();
		result.put("status", 0);
		result.put("message", "支付失败");
		return result;
	}

	public void insertCouponDetail(Integer couponNum,Long couponUserId,Long couponId,RdMmBasicInfo shopMember){
		Coupon coupon = couponService.find(couponId);

		for (int i=0;i<couponNum;i++){
			CouponDetail couponDetail = new CouponDetail();
			couponDetail.setId(twiterIdService.getTwiterId());
			couponDetail.setRdCouponUserId(couponUserId);
			couponDetail.setCouponId(coupon.getId());
			couponDetail.setCouponSn("YH"+twiterIdService.getTwiterId());
			couponDetail.setCouponName(coupon.getCouponName());
			couponDetail.setReceiveId(shopMember.getMmCode());
			couponDetail.setReceiveNickName(shopMember.getMmNickName());
			couponDetail.setReceiveTime(new Date());
			couponDetail.setHoldId(shopMember.getMmCode());
			couponDetail.setHoldNickName(shopMember.getMmNickName());
			couponDetail.setHoldTime(new Date());
			couponDetail.setUseStartTime(coupon.getUseStartTime());
			couponDetail.setUseEndTime(coupon.getUseEndTime());
			couponDetail.setUseState(2);

		}
	}

}
