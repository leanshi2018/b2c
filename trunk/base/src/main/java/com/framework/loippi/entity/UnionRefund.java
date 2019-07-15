package com.framework.loippi.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UnionRefund implements Serializable {
	/**
	 * 交易号（如1001004900005578936）
	 */
	private String tradeNo;
	/**
	 * 商户订单号
	 */
	private String orderId;
	/**
	 * 渠道类型 05：语音 07：互联网 08：移动 
	 */
	private String channelType;
	/**
	 * 前台通知地址
	 */
	private String frontUrl;
	/**
	 * 后台通知地址
	 */
	private String backUrl;
	/**
	 * 退款金额 单位为分
	 */
	private String txnAmt;
}
