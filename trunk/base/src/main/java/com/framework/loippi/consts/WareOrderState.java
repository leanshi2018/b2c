package com.framework.loippi.consts;

/**
 * @author :ldq
 * @date:2020/9/16
 * @description:dubbo com.framework.loippi.consts
 */
public final class WareOrderState {
	/**
	 * 调拨订单
	 */
	public final static int ORDER_TYPE_ALLOCATION = 8;
	/**
	 * 后台发货
	 */
	public final static int ORDER_TYPE_ADHUST = 9;
	/**
	 * 订单状态已取消
	 */
	public final static int ORDER_STATE_CANCLE = 0;

	/**
	 * 订单状态待审核
	 */
	public final static int ORDER_STATE_NO_AUDIT = 5;

	/**
	 * 订单状态待付款
	 */
	public final static int ORDER_STATE_NO_PATMENT = 10;

	/**
	 * 订单状态待发货
	 */
	public final static int ORDER_STATE_UNFILLED = 20;

	/**
	 * 订单状态待收货
	 */
	public final static int ORDER_STATE_NOT_RECEIVING = 30;

	/**
	 * 订单状态交易完成
	 */
	public final static int ORDER_STATE_FINISH = 40;

	/**
	 * 订单状态已提交(货到付款)
	 */
	public final static int ORDER_STATE_SUBMIT = 50;

	/**
	 * 订单状态已确认(货到付款)
	 */
	public final static int ORDER_STATE_CONFIRM = 60;

	/**
	 * 订单付款状态未付款
	 */
	public final static int PAYMENT_STATE_NO = 0;

	/**
	 * 订单付款状态已付款
	 */
	public final static int PAYMENT_STATE_YES = 1;

}
