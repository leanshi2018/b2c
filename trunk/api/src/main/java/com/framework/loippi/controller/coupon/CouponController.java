package com.framework.loippi.controller.coupon;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.integration.RdMmIntegralRule;
import com.framework.loippi.entity.order.ShopOrderPay;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.result.app.coupon.CouponPaySubmitResult;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.service.coupon.CouponPayDetailService;
import com.framework.loippi.service.coupon.CouponService;
import com.framework.loippi.service.integration.RdMmIntegralRuleService;
import com.framework.loippi.service.user.RdMmAccountInfoService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.Constants;
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

}
