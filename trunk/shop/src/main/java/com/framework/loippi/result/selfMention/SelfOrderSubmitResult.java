package com.framework.loippi.result.selfMention;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import com.framework.loippi.entity.ware.RdWareOrder;

/**
 * @author :ldq
 * @date:2020/9/16
 * @description:dubbo com.framework.loippi.result.selfMention
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelfOrderSubmitResult {

	/**
	 * 支付订单编码
	 */
	private RdWareOrder wareOrder;

	/**
	 * 用户购物积分
	 */
	private BigDecimal integration;
	/**
	 * 比例
	 */
	private Double proportion;

	/**
	 * 0默认未支付1已支付
	 */
	private String apiPayState;
	/**
	 * 是否需要支付（现金或者积分抵扣）
	 * 0不需要  1需要
	 */
	private Integer flagState;

}
