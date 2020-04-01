package com.framework.loippi.result.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author :ldq
 * @date:2020/3/11
 * @description:dubbo com.framework.loippi.result.order
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppletsPayTLResult {

	/**
	 * 状态
	 */
	private String payStatus;
	/**
	 * 商户订单号（支付订单
	 */
	private String bizOrderNo;
	/**
	 * 交易编号
	 */
	private String tradeNo;
	/**
	 * 微信 APP 支付 返回信息
	 */
	private Map<String, Object> weiXinStr;
	/**
	 * 扫码支付信息/ JS 支付串信息（微信、支付宝、QQ 钱包）/微信小程序/微信原生 H5 支付串信息/支付宝原生 APP 支付串信息
	 */
	private String payInfo;
	/**
	 * 交易验证方式 当支付方式为收银宝快捷且 需验证短信验证码时才返回，返回值为“1”表示需继续调用 【确认支付（后台+短信验证码确认）】
	 */
	private Long validateType;

}
