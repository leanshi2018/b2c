package com.framework.loippi.controller.coupon;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import com.framework.loippi.entity.coupon.CouponDetail;
import com.framework.loippi.entity.coupon.CouponPayDetail;
import com.framework.loippi.entity.coupon.CouponTransLog;
import com.framework.loippi.entity.coupon.CouponUser;
import com.framework.loippi.entity.integration.RdMmIntegralRule;
import com.framework.loippi.entity.order.ShopOrderPay;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.param.coupon.ConponPayDetailListResult;
import com.framework.loippi.param.coupon.ConponPayDetailResult;
import com.framework.loippi.result.app.coupon.CouponDetailListResult;
import com.framework.loippi.result.app.coupon.CouponPaySubmitResult;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.result.common.coupon.CouponTransInfoResult;
import com.framework.loippi.result.common.coupon.CouponTransferResult;
import com.framework.loippi.service.alipay.AlipayMobileService;
import com.framework.loippi.service.coupon.CouponDetailService;
import com.framework.loippi.service.coupon.CouponPayDetailService;
import com.framework.loippi.service.coupon.CouponService;
import com.framework.loippi.service.coupon.CouponTransLogService;
import com.framework.loippi.service.coupon.CouponUserService;
import com.framework.loippi.service.integration.RdMmIntegralRuleService;
import com.framework.loippi.service.order.ShopOrderPayService;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.trade.ShopMemberPaymentTallyService;
import com.framework.loippi.service.union.UnionpayService;
import com.framework.loippi.service.user.RdMmAccountInfoService;
import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.service.user.RdMmRelationService;
import com.framework.loippi.service.user.RdRanksService;
import com.framework.loippi.service.wechat.WechatMobileService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
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
	private CouponDetailService couponDetailService;
	@Resource
	private CouponPayDetailService couponPayDetailService;
	@Resource
	private CouponService couponService;
	@Resource
	private CouponTransLogService couponTransLogService;
	@Resource
	private CouponUserService couponUserService;
	@Resource
	private RdMmBasicInfoService rdMmBasicInfoService;
	@Resource
	private RdMmRelationService rdMmRelationService;
	@Resource
	private RdRanksService rdRanksService;
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

		return ApiUtils.success(coupon);
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
		Integer personLimitNum = 0;
		if (coupon.getPersonLimitNum()!=null){
			personLimitNum = coupon.getPersonLimitNum();//每个会员限制领取的张数，0为不限
		}
		Long totalLimitNum = -1l;
		if (coupon.getTotalLimitNum()!=null){
			totalLimitNum = coupon.getTotalLimitNum();//优惠券总发行数量 -1代表不限制
		}
		Long receivedNum = 0l;
		if (coupon.getTotalLimitNum()!=null){
			receivedNum = coupon.getReceivedNum();//已发放优惠券数量
		}
		String rankLimit = coupon.getRankLimit();//领取级别限制 多种级别已逗号分隔
		String[] rankList = rankLimit.split(",");
		int r = 10000;
		for (String rank : rankList) {
			if (rank.equals(rdMmRelation.getRank()+"")){
				r = 10001;
			}
		}

		Calendar date = Calendar.getInstance();
		date.setTime(new Date());

		Calendar begin = Calendar.getInstance();
		begin.setTime(startTime);

		Calendar end = Calendar.getInstance();
		end.setTime(endTime);

		if (date.after(begin) && date.before(end)) {
			System.out.println("在区间");
			if (personLimitNum==0||personLimitNum<=couponNumber){
				if (totalLimitNum==-1l){
					if (r==10001){
						//提交订单,返回订单支付实体
						ShopOrderPay orderPay = couponPayDetailService.addOrderReturnPaySn(member.getMmCode(),couponId,couponNumber);
						List<RdMmIntegralRule> rdMmIntegralRuleList = rdMmIntegralRuleService
								.findList(Paramap.create().put("order", "RID desc"));
						RdMmIntegralRule rdMmIntegralRule = new RdMmIntegralRule();
						if (rdMmIntegralRuleList != null && rdMmIntegralRuleList.size() > 0) {
							rdMmIntegralRule = rdMmIntegralRuleList.get(0);
						}
						return ApiUtils.success(CouponPaySubmitResult.build(rdMmIntegralRule,orderPay,rdMmAccountInfoService.find("mmCode", member.getMmCode())));
					}else {
						return ApiUtils.error("购买级别不符");
					}
				}else {
					if ((totalLimitNum-receivedNum)>0 && (totalLimitNum-receivedNum)>=couponNumber){
						if (r==10001){
							//提交订单,返回订单支付实体
							ShopOrderPay orderPay = couponPayDetailService.addOrderReturnPaySn(member.getMmCode(),couponId,couponNumber);
							List<RdMmIntegralRule> rdMmIntegralRuleList = rdMmIntegralRuleService
									.findList(Paramap.create().put("order", "RID desc"));
							RdMmIntegralRule rdMmIntegralRule = new RdMmIntegralRule();
							if (rdMmIntegralRuleList != null && rdMmIntegralRuleList.size() > 0) {
								rdMmIntegralRule = rdMmIntegralRuleList.get(0);
							}
							return ApiUtils.success(CouponPaySubmitResult.build(rdMmIntegralRule,orderPay,rdMmAccountInfoService.find("mmCode", member.getMmCode())));
						}else {
							return ApiUtils.error("购买级别不符");
						}
					}else{
						return ApiUtils.error("剩余购买数量为"+(totalLimitNum-receivedNum)+"张");
					}
				}
			}else {
				return ApiUtils.error("购买数量最大为"+couponNumber+"张");
			}
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

		List<CouponPayDetail> orderList = couponPayDetailService.findList("paySn", paysn);
		if (CollectionUtils.isEmpty(orderList)) {
			return ApiUtils.error("订单不存在");
		}
		if (orderList.get(0).getCouponOrderState()==0){
			return ApiUtils.error("订单已取消");
		}

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
			ShopOrderPay pay = orderPayService.findCouponBySn(paysn);
			//处理积分支付
			couponPayDetailService.ProcessingIntegralsCoupon(paysn, integration, shopMember, pay, shoppingPointSr);
		}

		System.out.println("##########################################");
		System.out
				.println("###  订单支付编号：" + paysn + "  |  支付方式名称：" + paymentCode + " |  支付方式索引id：" + paymentId + "#########");
		System.out.println("##########################################");
		ShopOrderPay pay = orderPayService.findCouponBySn(paysn);

		PayCommon payCommon = new PayCommon();
		payCommon.setOutTradeNo(pay.getPaySn());
		payCommon.setPayAmount(pay.getPayAmount());
		payCommon.setTitle("订单支付");
		payCommon.setBody(pay.getPaySn() + "订单支付");
		payCommon.setNotifyUrl(server + "/api/paynotify/notifyMobile/" + paymentCode + "/" + paysn + ".json");
		payCommon.setReturnUrl(server + "/payment/payfront");
		String sHtmlText = "";
		Map<String, Object> model = new HashMap<String, Object>();
		if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("alipayMobilePaymentPlugin")) {
			couponPayDetailService.updateByPaySn(paysn, Long.valueOf(paymentId));
			//保存支付流水记录
			System.out.println("dd:" + PaymentTallyState.PAYMENTTALLY_TREM_PC);
			paymentTallyService.savePaymentTallyCoupon(paymentCode, "支付宝", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 1);
			//修改订单付款信息
			sHtmlText = alipayMobileService.toPay(payCommon);//TODO
			model.put("tocodeurl", sHtmlText);
			model.put("orderSn", pay.getOrderSn());
		} else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("YL")) {
			//修改订单付款信息
			couponPayDetailService.updateByPaySn(paysn, Long.valueOf(paymentId));
			//保存支付流水记录
			paymentTallyService.savePaymentTallyCoupon(paymentCode, "银联", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 1);
			sHtmlText = unionpayService.prePay(payCommon, request);//构造提交银联的表单
			model.put("tocodeurl", sHtmlText);
			model.put("orderSn", pay.getOrderSn());
		} else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("weixinMobilePaymentPlugin")) {
			//修改订单付款信息
			couponPayDetailService.updateByPaySn(paysn, Long.valueOf(paymentId));
			//保存支付流水记录
			paymentTallyService.savePaymentTallyCoupon(paymentCode, "微信支付", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 1);
			String tocodeurl = wechatMobileService.toPay(payCommon);//微信扫码url
			model.put("tocodeurl", tocodeurl);
			model.put("orderSn", pay.getOrderSn());
		} else if (StringUtils.isNotEmpty(paysn) && paymentCode.equals("pointsPaymentPlugin")) {//积分全额支付
			// TODO: 2018/12/18
			//积分全额支付判断
			if (paymentCode.equals("pointsPaymentPlugin")) {
				if (pay.getPayAmount().compareTo(new BigDecimal(0)) != 0) {
					return ApiUtils.error("该订单不符合购物积分全抵现,请选择支付方式");
				}
			}
			paymentTallyService.savePaymentTallyCoupon(paymentCode, "积分全抵扣", pay, PaymentTallyState.PAYMENTTALLY_TREM_MB, 2);
			Map<String, Object> data = couponPayDetailService.updateOrderpay(payCommon, member.getMmCode(), "在线支付-购物积分", paymentCode, paymentId);
			model.putAll(data);
		}

		return ApiUtils.success(model);
	}

	/**
	 * 获取当前登录用户指定优惠券信息
	 * @param request
	 * @param couponId
	 * @return
	 */
	@RequestMapping(value = "/detail/couponid", method = RequestMethod.POST)
	public String getCouponById(HttpServletRequest request, Long couponId,@RequestParam(required = false,value = "pageSize",defaultValue = "10")Integer pageSize,
								@RequestParam(required = false,value = "pageNum",defaultValue = "1")Integer pageNum) {
		AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
		if(member==null){
			return ApiUtils.error("请登录后进行此操作");
		}
		if(couponId==null){
			return ApiUtils.error("请选择需要赠送的优惠券");
		}
		Coupon coupon = couponService.find(couponId);
		if(coupon==null){
			return ApiUtils.error("当前优惠券不存在");
		}
		if(coupon.getStatus()!=2){
			return ApiUtils.error("当前优惠券状态不正确");
		}
		if(coupon.getWhetherPresent()!=1){
			return ApiUtils.error("当前优惠券不可以赠送他人");
		}
		if(new Date().getTime()>coupon.getUseEndTime().getTime()){
			return ApiUtils.error("当前优惠券已过期，不可赠送他人");
		}
		List<CouponUser> list = couponUserService.findList(Paramap.create().put("mCode", member.getMmCode()).put("couponId", couponId));
		if(list==null||list.size()==0){
			return ApiUtils.error("当前登录用户无该优惠券记录");
		}
		CouponUser couponUser = list.get(0);
		Pageable pageable = new Pageable();
		pageable.setPageSize(pageSize);
		pageable.setPageNumber(pageNum);
		pageable.setOrderDirection(Order.Direction.DESC);
		pageable.setOrderProperty("trans_time");
		pageable.setParameter(Paramap.create().put("turnId", member.getMmCode()));
		Page<CouponTransLog> logs =couponTransLogService.findByPage(pageable);
		List<CouponTransLog> transLogs = logs.getContent();
		List<String> mmCodes = new ArrayList();
		if(transLogs!=null&&transLogs.size()>0){
			for (CouponTransLog log : transLogs) {
				mmCodes.add(log.getAcceptId());
			}
		}
		List<RdMmBasicInfo> rdMmBasicInfoList = new ArrayList<>();
		List<RdMmRelation> rdMmRelationList = new ArrayList<>();
		if (mmCodes != null && mmCodes.size() > 0) {
			rdMmBasicInfoList = rdMmBasicInfoService.findList("mmCodes", mmCodes);
			rdMmRelationList = rdMmRelationService.findList("mmCodes", mmCodes);
		}
		List<RdRanks> shopMemberGradeList = rdRanksService.findAll();
		CouponTransInfoResult result = CouponTransInfoResult.build(coupon, couponUser,rdMmBasicInfoList,rdMmRelationList,shopMemberGradeList);
		return ApiUtils.success(result);
	}

	/**
	 * 优惠券转让
	 * @param request
	 * @param couponId 优惠券id
	 * @param transNum 转让数量
	 * @param recipientCode 接收人会员编号
	 * @return
	 */
	@RequestMapping(value = "/trans/coupon", method = RequestMethod.POST)
	public String transactionCoupon(HttpServletRequest request, Long couponId,Integer transNum,String recipientCode) {
		AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
		if(member==null){
			return ApiUtils.error("请登录后进行此操作");
		}
		if(couponId==null){
			return ApiUtils.error("请选择需要赠送的优惠券");
		}
		Coupon coupon = couponService.find(couponId);
		if(coupon==null){
			return ApiUtils.error("当前优惠券不存在");
		}
		if(coupon.getStatus()!=2){
			return ApiUtils.error("当前优惠券状态不正确");
		}
		if(coupon.getWhetherPresent()!=1){
			return ApiUtils.error("当前优惠券不可以赠送他人");
		}
		if(new Date().getTime()>coupon.getUseEndTime().getTime()){
			return ApiUtils.error("当前优惠券已过期，不可赠送他人");
		}
		List<CouponUser> list = couponUserService.findList(Paramap.create().put("mCode", member.getMmCode()).put("couponId", couponId));
		if(list==null||list.size()==0){
			return ApiUtils.error("当前登录用户无该优惠券记录");
		}
		CouponUser couponUser = list.get(0);
		Integer ownNum = couponUser.getOwnNum();
		if(transNum>ownNum){
			return ApiUtils.error("当前转让优惠券数量大于会员拥有数量");
		}
		try {
			HashMap<String,Object> result=couponService.transactionCoupon(member.getMmCode(),member.getNickname(),recipientCode,coupon,couponUser,transNum);
			Boolean flag = (Boolean) result.get("flag");
			String msg = (String) result.get("msg");
			if (flag){
				CouponTransferResult data = (CouponTransferResult) result.get("object");
				return ApiUtils.success(data);
			}else {
				return ApiUtils.error(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ApiUtils.error("网络异常，请稍后重试");
		}
	}

	/**
	 * 个人优惠券列表
	 * @param request
	 * @param stateType 优惠券状态 1.已使用 2.未使用 3.已过期
	 * @return
	 */
	@RequestMapping(value = "/couponList", method = RequestMethod.POST)
	public String couponList(HttpServletRequest request, @RequestParam(required = true,value = "stateType") Integer stateType,
							 @RequestParam(required = false,value = "pageSize",defaultValue = "20")Integer pageSize,
							 @RequestParam(required = false,value = "pageNum",defaultValue = "1")Integer pageNum
			) {
		if(stateType==null){
			return ApiUtils.error("请传入需要查询的优惠券状态类型");
		}
		AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
		if(member==null){
			return ApiUtils.error("请登录后进行优惠券查询操作");
		}
		Pageable pageable = new Pageable();
		pageable.setPageSize(pageSize);
		pageable.setPageNumber(pageNum);
		pageable.setOrderDirection(Order.Direction.ASC);
		pageable.setOrderProperty("use_end_time");
		pageable.setParameter(Paramap.create().put("holdId",member.getMmCode()).put("useState",stateType));
		Page<CouponDetail> page = couponDetailService.findByPage(pageable);
		List<CouponDetail> couponDetails = page.getContent();
		if(couponDetails!=null&&couponDetails.size()>0){
			HashMap<Long, Coupon> map = new HashMap<>();
			for (CouponDetail couponDetail : couponDetails) {
				Coupon coupon = couponService.find(couponDetail.getCouponId());
				map.put(couponDetail.getCouponId(),coupon);
			}
			return ApiUtils.success(CouponDetailListResult.build(couponDetails,map));
		}else {
			return ApiUtils.success(new ArrayList<CouponDetailListResult>());
		}
	}

	/**
	 * 优惠券订单列表
	 * @param request
	 * @param pageNumber
	 * @param pageSize
	 * @param couponOrderState 1:已退款;10:待付款;40:交易完成;
	 * @return
	 */
	@RequestMapping(value = "/lists/couponPayDetail", method = RequestMethod.POST)
	public String memberConponPayDetailList(HttpServletRequest request,
											@RequestParam(defaultValue = "1") Integer pageNumber,
											@RequestParam(defaultValue = "10") Integer pageSize,
											Integer couponOrderState) {
		AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(com.framework.loippi.consts.Constants.CURRENT_USER);
		if (couponOrderState==null || couponOrderState==-1){
			couponOrderState=null;
		}
		Pageable pager = new Pageable(pageNumber, pageSize);
		Map<String, Object> params = new HashMap<>();
		params.put("mmCode", member.getMmCode());
		List<CouponPayDetail> lists = null;
		if (couponOrderState==null){
			params.put("couponOrderState", couponOrderState);
			lists = couponPayDetailService.findList(Paramap.create().put("receiveId", member.getMmCode()).put("couponOrderState", couponOrderState));
			if(lists==null||lists.size()==0){
				return ApiUtils.error("无购买优惠券订单记录");
			}
		}else {
			if (couponOrderState==1){
				params.put("refundState", couponOrderState);
				lists = couponPayDetailService.findList(Paramap.create().put("receiveId", member.getMmCode()).put("refundState", couponOrderState));
				if(lists==null||lists.size()==0){
					return ApiUtils.error("无退款优惠券订单记录");
				}
			}else {
				params.put("couponOrderState", couponOrderState);
				lists = couponPayDetailService.findList(Paramap.create().put("receiveId", member.getMmCode()).put("couponOrderState", couponOrderState));
				if(lists==null||lists.size()==0){
					return ApiUtils.error("无购买优惠券订单记录");
				}
			}
		}

		pager.setOrderDirection(Order.Direction.DESC);
		pager.setOrderProperty("create_time");
		pager.setParameter(params);
		Page<CouponPayDetail> byPage = couponPayDetailService.findByPage(pager);
		return ApiUtils.success(ConponPayDetailListResult.build(byPage.getContent(),couponService.findAll()));
	}

	/**
	 * 优惠券订单详情
	 * @param request
	 * @param couponOrderId
	 * @return
	 */
	@RequestMapping(value = "/detail/couponPayDetail", method = RequestMethod.POST)
	public String memberConponPayDetailInfo(HttpServletRequest request,Long couponOrderId) {

		AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(com.framework.loippi.consts.Constants.CURRENT_USER);
		if (couponOrderId==null){
			return ApiUtils.error("该订单Id为空,请传正确的订单Id");
		}
		CouponPayDetail couponPayDetail = couponPayDetailService.find(couponOrderId);
		if (couponPayDetail==null){
			return ApiUtils.error("该订单不存在");
		}
		Long couponId = couponPayDetail.getCouponId();
		Coupon coupon = couponService.find(couponId);
		if (coupon==null){
			return ApiUtils.error("该订单优惠券已不存在");
		}
		List<CouponDetail> couponDetailList = couponDetailService.findList("buyOrderId", couponPayDetail.getId());

		return ApiUtils.success(ConponPayDetailResult.build(couponPayDetail,coupon,couponDetailList));
	}

}
