package com.framework.loippi.result.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
	 *
	 */
	private String paySign;
	/**
	 *
	 */
	private String signType;
	/**
	 * 随机字符串
	 */
	private String nonceStr;
	/**
	 * 应用 ID
	 */
	private String appId;
	/**
	 * 时间戳
	 */
	private String timeStamp;
	/**
	 *
	 */
	private String packageS;

}
