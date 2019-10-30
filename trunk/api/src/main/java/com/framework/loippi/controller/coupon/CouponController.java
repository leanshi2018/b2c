package com.framework.loippi.controller.coupon;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.framework.loippi.consts.PaymentTallyState;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.PayCommon;
import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.integration.RdMmIntegralRule;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.order.ShopOrderPay;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.result.app.coupon.CouponPaySubmitResult;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.service.alipay.AlipayMobileService;
import com.framework.loippi.service.coupon.CouponPayDetailService;
import com.framework.loippi.service.coupon.CouponService;
import com.framework.loippi.service.integration.RdMmIntegralRuleService;
import com.framework.loippi.service.order.ShopOrderPayService;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.trade.ShopMemberPaymentTallyService;
import com.framework.loippi.service.union.UnionpayService;
import com.framework.loippi.service.user.RdMmAccountInfoService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.service.wechat.WechatMobileService;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.Constants;
import com.framework.loippi.utils.Digests;
import com.framework.loippi.utils.Paramap;

/**
 * @author :ldq
 * @date:2019/10/21
 * @description:dubbo com.framework.loippi.controller.coupon
 */
@Controller
@ResponseBody
@RequestMapping("/api/coupon")
public class CouponController extends BaseController {

	@Resource
	private CouponPayDetailService couponPayDetailService;
	@Resource
	private CouponService couponService;
	@Resource
	private RdMmBasicInfoService rdMmBasicInfoService;
	@Resource
	private RdMmRelationService rdMmRelationService;
	@Resource
	private RdMmIntegralRuleService rdMmIntegralRuleService;
	@Resource
	private RdMmAccountInfoService rdMmAccountInfoService;
	@Resource
	private ShopOrderPayService orderPayService;
	@Resource
	private ShopOrderService orderService;
	@Resource
	private ShopMemberPaymentTallyService paymentTallyService;
	@Resource
	private AlipayMobileService alipayMobileService;
	@Resource
	private UnionpayService unionpayService;
	@Resource
	private WechatMobileService wechatMobileService;

	/**
	 * 优惠券详情
	 * @param request
	 * @param couponId
	 * @return
	 */
	@RequestMapping(value = "/coupondetail", method = RequestMethod.POST)
	public String detail(HttpServletRequest request, Long couponId) {
		String sessionId = request.getHeader(Constants.USER_SESSION_ID);
		if (couponId == null) {
			return jsonFail();
		}
		//商品基本详情对象
		Coupon coupon = couponService.find(couponId);
		if (coupon == null) {
			return jsonFail("优惠券不存在");
		}

		Map<String, Object> resultMap = new HashMap<>();
		resultMap.put("couponDetailInfo", coupon);
		return ApiUtils.success(resultMap);
	}

	/**
	 * 优惠券购买提交订单
	 * @param couponId  优惠券id
	 * @param couponNumber  购买个数
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/order/submit", method = RequestMethod.POST)
	@ResponseBody
	public String orderSubmit(@RequestParam Long couponId, Integer couponNumber,HttpServletRequest request) {
		if (couponId == null) {
			return jsonFail();
		}

		AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(com.framework.loippi.consts.Constants.CURRENT_USER);
		//RdMmBasicInfo member = rdMmBasicInfoService.findByMCode(memberId);

		//订单类型相关
		RdMmBasicInfo rdMmBasicInfo = rdMmBasicInfoService.find("mmCode", member.getMmCode());
		RdMmRelation rdMmRelation = rdMmRelationService.find("mmCode", member.getMmCode());

		//优惠券信息
		Coupon coupon = couponService.find(couponId);
		Date startTime = coupon.getSendStartTime();//优惠券发放开始时间
		Date endTime = coupon.getSendEndTime();//优惠券发放结束时间

		Calendar date = Calendar.getInstance();
		date.setTime(new Date());

		Calendar begin = Calendar.getInstance();
		begin.setTime(startTime);

		Calendar end = Calendar.getInstance();
		end.setTime(endTime);

		if (date.after(begin) && date.before(end)) {
			System.out.println("在区间");
			//提交订单,返回订单支付实体
			ShopOrderPay orderPay = couponPayDetailService.addOrderReturnPaySn(member.getMmCode(),couponId,couponNumber);
			List<RdMmIntegralRule> rdMmIntegralRuleList = rdMmIntegralRuleService
					.findList(Paramap.create().put("order", "RID desc"));
			RdMmIntegralRule rdMmIntegralRule = new RdMmIntegralRule();
			if (rdMmIntegralRuleList != null && rdMmIntegralRuleList.size() > 0) {
				rdMmIntegralRule = rdMmIntegralRuleList.get(0);
			}
			return ApiUtils.success(CouponPaySubmitResult.build(rdMmIntegralRule,orderPay,rdMmAccountInfoService.find("mmCode", member.getMmCode())));
		} else {
			System.out.println("不在区间");
			if (date.before(begin)){
				return ApiUtils.error("该优惠券还未到发放时间");
			}
			if (date.after(end)){
				return ApiUtils.error("该优惠券发放已结束");
			}
			return ApiUtils.error("该优惠券发放出现错误");
		}

	}

	/**
	 * 优惠券付款
	 *
	 * @param paysn 支付订单编码
	 * @param paymentCode 支付代码名称: ZFB YL weiscan
	 * @param paymentId 支付方式索引id
	 * @param integration 积分
	 * @param paypassword 支付密码
	 */
	@RequestMapping("/order/payCoupon")
	@ResponseBody
	public String payOrderCoupon(@RequestParam(value = "paysn") String paysn,
								 @RequestParam(defaultValue = "pointsPaymentPlugin") String paymentCode,
								 @RequestParam(defaultValue = "0") String paymentId,
								 @RequestParam(defaultValue = "0") Integer integration,
								 @RequestParam(defaultValue = "0") String paypassword,
								 HttpServletRequest request) {

		AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(com.framework.loippi.consts.Constants.CURRENT_USER);
		RdMmBasicInfo shopMember = rdMmBasicInfoService.find("mmCode", member.getMmCode());
		RdMmAccountInfo rdMmAccountInfo = rdMmAccountInfoService.find("mmCode", member.getMmCode());
		//处理购物积分
		//获取购物积分购物比例
		List<RdMmIntegralRule> rdMmIntegralRuleList = rdMmIntegralRuleService
				.findList(Paramap.create().put("order", "RID desc"));
		RdMmIntegralRule rdMmIntegralRule = new RdMmIntegralRule();
		if (rdMmIntegralRuleList != null && rdMmIntegralRuleList.size() > 0) {
			rdMmIntegralRule = rdMmIntegralRuleList.get(0);
		}
		int shoppingPointSr = Optional.ofNullable(rdMmIntegralRule.getShoppingPointSr()).orElse(0);
		if (integration != 0) {
			if (rdMmAccountInfo.getPaymentPwd() == null) {
				return ApiUtils.error("你还未设置支付密码");
			}
			if (!Digests.validatePassword(paypassword, rdMmAccountInfo.getPaymentPwd())) {
				return ApiUtils.error("支付密码错误");
			}
			if (rdMmAccountInfo.getWalletStatus() != 0) {
				return ApiUtils.error("购物积分账户状态未激活或者已被冻结");
			}
			ShopOrderPay pay = orderPayService.findBySn(paysn);
			//处理积分支付
			orderService.ProcessingIntegralsCoupon(paysn, integration, shopMember, pay, shoppingPointSr);
		}

		List<ShopOrder> orderList = orderService.findList("paySn", paysn);
		if (CollectionUtils.isEmpty(orderList)) {
			return ApiUtils.error("订单不存在");
		}

		System.out.println("##########################################");
		System.out
				.println("###  订单支付编号：" + paysn + "  |  支付方式名称：" + paymentCode + " |  支付方式索引id：" + paymentId + "#########");
		System.out.println("##########################################");
		ShopOrderPay pay = orderPayService.findBySn(paysn);

		PayCommon payCommon = new PayCommon();
		payCommon.setOutTradeNo(pay.getPaySn());
		if ("balancePaymentPlugin".equals(paymentCode)) {
			payCommon.setPayAmount(pay.getPayAmount());
		} else {
			//payCommon.setPayAmount(new BigDecimal(0.01));
			payCommon.setPayAmount(pay.getPayAmount());
		}
		payCommon.setTitle("订单支付");
		payCommon.setBody(pay.getPaySn() + "订单支付");
		payCommon.setNotifyUrl(server + "/api/paynotify/notifyMobile/" + paymentCode + "/" + paysn + ".json");
		payCommon.setReturnUrl(server + "/payment/payfront");
		String sHtmlText = "";
		Map<String, Object> model = new HashMap<String, Object>();
		if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("alipayMobilePaymentPlugin")) {
			orderService.updateByPaySn(paysn, Long.valueOf(paymentId));
			//保存支付流水记录
			System.out.println("dd:" + PaymentTallyState.PAYMENTTALLY_TREM_PC);
			paymentTallyService.savePaymentTally(paymentCode, "支付宝", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 1);
			//修改订单付款信息
			sHtmlText = alipayMobileService.toPay(payCommon);//TODO
			model.put("tocodeurl", sHtmlText);
			model.put("orderSn", pay.getOrderSn());
		} else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("YL")) {
			//修改订单付款信息
			orderService.updateByPaySn(paysn, Long.valueOf(paymentId));
			//保存支付流水记录
			paymentTallyService.savePaymentTally(paymentCode, "银联", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 1);
			sHtmlText = unionpayService.prePay(payCommon, request);//构造提交银联的表单
			model.put("tocodeurl", sHtmlText);
			model.put("orderSn", pay.getOrderSn());
		} else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("weixinMobilePaymentPlugin")) {
			//修改订单付款信息
			orderService.updateByPaySn(paysn, Long.valueOf(paymentId));
			//保存支付流水记录
			paymentTallyService.savePaymentTally(paymentCode, "微信支付", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 1);
			String tocodeurl = wechatMobileService.toPay(payCommon);//微信扫码url
			model.put("tocodeurl", tocodeurl);
			model.put("orderSn", pay.getOrderSn());
		} else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("balancePaymentPlugin")) {//余额支付
//            Map<String, Object> data = orderService.payWallet(payCommon, member.getMmCode());
//            model.putAll(data);
		} else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("pointsPaymentPlugin")) {//积分全额支付
			// TODO: 2018/12/18
			//积分全额支付判断
			if (paymentCode.equals("pointsPaymentPlugin")) {
				if (pay.getPayAmount().compareTo(new BigDecimal(0)) != 0) {
					return ApiUtils.error("该订单不符合购物积分全抵现,请选择支付方式");
				}
			}
			paymentTallyService.savePaymentTally(paymentCode, "积分全抵扣", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 2);
			Map<String, Object> data = orderService
					.updateOrderpay(payCommon, member.getMmCode(), "在线支付-购物积分", paymentCode, paymentId);
			model.putAll(data);
		} else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("cashOnDeliveryPlugin")) {//货到付款
			// TODO: 2018/12/18
			paymentTallyService.savePaymentTally(paymentCode, "货到付款", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 2);
			Map<String, Object> data = orderService
					.updateOrderpay(payCommon, member.getMmCode(), "货到付款", paymentCode, paymentId);
			model.putAll(data);
		}

		return ApiUtils.success(model);
	}

}
