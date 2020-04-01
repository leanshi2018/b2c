package com.framework.loippi.service.wechat.impl;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.framework.loippi.service.PaymentService;
import com.framework.loippi.service.TSystemPluginConfigService;
import com.framework.loippi.service.wechat.WechatAppletsService;
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
		System.out.println("通联支付回调数据："+response);



		return null;
	}
}
