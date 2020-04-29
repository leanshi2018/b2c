package com.framework.loippi.controller.allinpay;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.framework.loippi.entity.trade.ShopRefundReturn;
import com.framework.loippi.service.PaymentService;
import com.framework.loippi.service.alipay.AlipayMobileService;
import com.framework.loippi.service.order.ShopOrderService;
import com.framework.loippi.service.trade.ShopRefundReturnService;
import com.framework.loippi.service.wallet.RdBizPayService;
import com.framework.loippi.service.wallet.RdMmWithdrawLogService;
import com.framework.loippi.service.wechat.WechatAppletsService;
import com.framework.loippi.utils.JacksonUtil;

/**
 * @author :ldq
 * @date:2020/4/3
 * @description:dubbo com.framework.loippi.controller.allinpay
 */
@Slf4j
@Controller
@RequestMapping("/admin/paynotify")
public class TLNotifyController {

	@Resource
	private RdMmWithdrawLogService rdMmWithdrawLogService;
	@Resource
	private ShopRefundReturnService refundReturnService;
	@Resource
	private ShopOrderService orderService;
	@Resource
	private RdBizPayService rdBizPayService;
	@Resource
	private WechatAppletsService wechatAppletsService;
	@Resource
	private PaymentService paymentService;
	@Resource
	private AlipayMobileService alipayMobileService;
	/**
	 * 提现回调
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/withdrawBank.jhtml")
	public void withdrawBank(HttpServletRequest request,HttpServletResponse response) throws IOException{


		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


		System.out.println("进来提现回调");

		//request中的param
		String rps = request.getParameter("rps");
		System.out.println("************************");
		System.out.println("提现回调："+rps);
		System.out.println("************************");

		/*
		{"returnValue":{"buyerBizUserId":"900013805",
						"amount":1000,
						"orderNo":"1249537142296711168",
						"extendInfo":"",
						"payDatetime":"2020-04-13 11:17:34",
						"acct":"621485******2516",
						"bizOrderNo":"W6655302743650996224"},
			"method":"pay","service":"OrderService","status":"OK"}
		* */
		Map map = (Map) JSON.parse(rps);
		//Map<String, Object> map = JacksonUtil.convertMap(rps);
		String status = map.get("status").toString();
		System.out.println(status);
		String returnValue = map.get("returnValue").toString();
		Map returnMap = (Map) JSON.parse(returnValue);

		String orderNo = returnMap.get("orderNo").toString();
		String bizOrderNo = returnMap.get("bizOrderNo").toString();
		String buyerBizUserId = returnMap.get("buyerBizUserId").toString();
		String acct = returnMap.get("acct").toString();

		if(status.equals("error")){
			System.out.println("失败！");
			rdMmWithdrawLogService.updateStatusBySnAndMCode(1,bizOrderNo,acct);
		}
		if(status.equals("pending")){
			rdMmWithdrawLogService.updateStatusBySnAndMCode(0,bizOrderNo,acct);
		}
		if(status.equals("OK")){
			System.out.println("成功！");
			rdMmWithdrawLogService.updateStatusBySnAndMCode(0,bizOrderNo,acct);
		}
	}

	/**
	 * 退款回调
	 * @param request
	 * @param response
	 */
	@RequestMapping(value ="/refundBank.jhtml")
	public void refundBank(HttpServletRequest request,HttpServletResponse response) throws IOException{
		System.out.println("进来退款回调");
		//request中的param
		String rps = request.getParameter("rps");
		System.out.println("************************");
		System.out.println("退款回调："+rps);
		System.out.println("************************");
		Map<String, Object> map = JacksonUtil.convertMap(rps);
		String status = (String) map.get("status");
		String returnValue = (String) map.get("returnValue");
		Map returnMap = (Map) JSON.parse(returnValue);

		String orderNo = returnMap.get("orderNo").toString();
		String bizOrderNo = returnMap.get("bizOrderNo").toString();
		//String buyerBizUserId = returnMap.get("buyerBizUserId").toString();

		ShopRefundReturn refundReturn = refundReturnService.find(Long.valueOf(bizOrderNo));
		String adminMsg = Optional.ofNullable(refundReturn.getAdminMessage()).orElse("");

		if(status.equals("error")){
			String message = Optional.ofNullable(map.get("message").toString()).orElse("");
			String oriOrderNo = Optional.ofNullable(map.get("oriOrderNo").toString()).orElse("");// 原通商云订单号
			String msg = adminMsg+","+message;
			refundReturnService.updateTlStatusById(bizOrderNo,0,msg);
		}
		if(status.equals("pending")){
			refundReturnService.updateTlStatusById(bizOrderNo,1,adminMsg);
		}
		if(status.equals("OK")){
			String buyerBizUserId = returnMap.get("buyerBizUserId").toString();//商户系统用户标识，商 户系统中唯一编号。 付款人
			String oriOrderNo = Optional.ofNullable(returnMap.get("oriOrderNo").toString()).orElse("");// 原通商云订单号
			String oriBizOrderNo = Optional.ofNullable(returnMap.get("oriBizOrderNo").toString()).orElse("");//原商户订单号
			Long amount = Optional.ofNullable(Long.valueOf(returnMap.get("amount").toString())).orElse(0l);//订单金额
			refundReturnService.updateTlStatusById(bizOrderNo,2,adminMsg);

			/*List<RdBizPay> rdBizPayList = rdBizPayService.findByPaysnAndStatus(oriBizOrderNo,1);
			RdBizPay rdBizPay = rdBizPayList.get(0);
			String bizPaySn = rdBizPay.getBizPaySn();
			ShopOrder order = orderService.findByBuyPaySn(bizPaySn);
			if (order!=null){
				if (order.getCutStatus()==6){
					if (order.getCutAmount().compareTo(new BigDecimal(0))==1){//大于0

					}
				}
			}*/


		}
	}


	/**
	 * 支付回调
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = "/notifyMobile.jhtml")
	public void notifyMobile(HttpServletRequest request,HttpServletResponse response) throws IOException{

		System.out.println("进来支付回调");

		//request中的param
		String rps = request.getParameter("rps");
		System.out.println("************************");
		System.out.println("支付回调："+rps);
		System.out.println("************************");

		Map map = (Map) JSON.parse(rps);
		//Map<String, Object> map = JacksonUtil.convertMap(rps);
		String status = map.get("status").toString();
		System.out.println(status);
		String returnValue = map.get("returnValue").toString();
		Map returnMap = (Map) JSON.parse(returnValue);

		String orderNo = returnMap.get("orderNo").toString();
		String bizOrderNo = returnMap.get("bizOrderNo").toString();
		String buyerBizUserId = returnMap.get("buyerBizUserId").toString();

		String[] split = bizOrderNo.split("WOMI");
		String paySn = split[0];

		//支付失败
		if(status.equals("error")) {
			System.out.println("失败！");
			paymentService.updatePayfailBack(paySn);
		}

		//支付成功
		if(status.equals("OK")){
			System.out.println("成功！");
			Long amount = Long.valueOf(returnMap.get("amount").toString())*100; //订单金额  单位：分
			paymentService.updatePayBack(paySn, orderNo, "applet_weichatpay",amount.toString());
		}
	}

	/**
	 * 支付宝回调
	 * @param request
	 * @param response
	 */
	@RequestMapping({"/alipayNotify/{sn}.jhtml"})
	public void alipayNotify(HttpServletRequest request,
							 @PathVariable String sn,//支付订单号paySn
							 HttpServletResponse response) throws IOException{

		System.out.println("进来了支付宝回调");
		PrintWriter out = null;
		String result = "failure";
		result = alipayMobileService.notifyCheck(request, sn);
		out.print(result);
	}


}
