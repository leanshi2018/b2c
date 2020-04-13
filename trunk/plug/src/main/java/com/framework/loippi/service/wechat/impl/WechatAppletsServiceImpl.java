package com.framework.loippi.service.wechat.impl;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.framework.loippi.service.PaymentService;
import com.framework.loippi.service.TSystemPluginConfigService;
import com.framework.loippi.service.wechat.WechatAppletsService;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.wechat.mobile.config.WXpayConfig;

/**
 * @author :ldq
 * @date:2020/3/24
 * @description:dubbo com.framework.loippi.service.wechat.impl
 */
@Service
@Slf4j
public class WechatAppletsServiceImpl implements WechatAppletsService {

	@Resource
	private PaymentService paymentService;
	@Resource
	private TSystemPluginConfigService tSystemPluginConfigService;

	@PostConstruct
	public void init() {
		WXpayConfig.initPayConfig(tSystemPluginConfigService.readPlug("weixinAppletsPaymentPlugin"));
	}

	@Override
	public String notifyCheck(HttpServletRequest request, HttpServletResponse response, String sn) {

		String[] womiSn = sn.split("WOMI");
		String paySn = womiSn[0];

		//通联支付回调数据
		String rps = request.getParameter("rps");
		System.out.println("************************");
		System.out.println("小程序支付回调："+rps);
		System.out.println("************************");
		Map<String, Object> map = JacksonUtil.convertMap(rps);
		String status = (String) map.get("status");
		//支付失败
		if(status.equals("error")) {
			paymentService.updatePayfailBack(paySn);
		}

		//支付成功
		if(status.equals("OK")){
			Map<String, Object> returnValue = (Map<String, Object>)map.get("returnValue");
			String orderNo = returnValue.get("orderNo").toString();//通商云订单号
			String bizOrderNo = returnValue.get("bizOrderNo").toString();//商户订单号（支付订单）
			String[] split = bizOrderNo.split("WOMI");
			String paySn1 = split[0];
			Long amount = Long.valueOf(returnValue.get("amount").toString())*100; //订单金额  单位：分
			paymentService.updatePayBack(paySn1, orderNo, "applet_weichatpay",amount.toString());
		}

		return "success";
	}
}
