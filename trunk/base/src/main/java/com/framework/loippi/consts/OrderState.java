package com.framework.loippi.consts;

public final class OrderState {

    /**
     * 平台微信
     */
    public final static int PLATFORM_WECHAT = 0;

    /**
     * 平台app
     */
    public final static int PLATFORM_APP = 1;

    /**
     * 平台PC
     */
    public final static int PLATFORM_PC = 2;

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

    /**
     * 订单退款状态无退款
     */
    public final static int REFUND_STATE_NO = 0;

    /**
     * 订单退款状态部分退款
     */
    public final static int REFUND_STATE_SOM = 1;


    /**
     * 订单退款状态全部退款
     */
    public final static int REFUND_STATE_ALL = 2;

    /**
     * 订单退货状态无退货
     */
    public final static int RETURN_STATE_NO = 0;


    /**
     * 订单退货状态部分退货
     */
    public final static int RETURN_STATE_SOM = 1;


    /**
     * 订单退货状态全部退货
     */
    public final static int RETURN_STATE_ALL = 2;

    /**
     * 订单退货状态无换货
     */
    public final static int BARTER_STATE_NO = 70;

    /**
     * 订单换货状态部分换货
     */
    public final static int BARTER_STATE_SOM = 80;

    /**
     * 订单换货状态全部换货
     */
    public final static int BARTER_STATE_ALL = 90;

    /**
     * 订单类型普通
     */
    public final static int ORDER_TYPE_ORDINARY = 0;

    /**
     * 订单类型-虚拟商品
     */
    public final static int ORDER_TYPE_VIRTUAL = 2;

    /**
     * 订单类型团购
     */
    public final static int ORDER_TYPE_GROUP = 1;

    /**
     * 订单类型秒杀
     */
    public final static int ORDER_TYPE_SECKILL = 3;

    /**
     * 订单类型混合
     */
    public final static int ORDER_TYPE_MIX = 5;

    /**
     * 订单类型促销订单
     */
    public static final int ORDER_TYPE_PROMOTION = 4;

    /**
     * 订单评论状态-未评论
     */
    public final static int ORDER_EVALUATION_NO = 0;

    /**
     * 订单评论状态-已评论
     */
    public final static int ORDER_EVALUATION_YES = 1;

    /**
     * 订单结算状态-未结算
     */
    public final static int ORDER_BALANCE_NO = 0;

    /**
     * 订单结算状态-已结算
     */
    public final static int ORDER_BALANCE_YES = 1;

    /**
     * 订单锁定状态:未锁定
     */
    public final static int ORDER_LOCK_STATE_NO = 0;

    /**
     * 订单锁定状态:锁定
     */
    public final static int ORDER_LOCK_STATE_YES = 1;

    /**
     * 订单项是否能申请售后:可以
     */
    public final static int ORDER_GOODS_IS_CUSTOMER_TRUE = 0;

    /**
     * 订单项是否能申请售后:不可以
     */
    public final static int ORDER_GOODS_IS_CUSTOMER_FALSE = 1;

    /**
     * 兑换码使用状态 未使用
     */
    public final static int ORDER_VIRTUAL_STATE_NO = 10;


    /**
     * 兑换码使用状态 已使用
     */
    public final static int ORDER_VIRTUAL_STATE_YES = 20;

    /**
     * 兑换码使用状态 已过期
     */
    public final static int ORDER_VIRTUAL_STATE_REF = 30;

    /**
     * 兑换码使用状态 已过期
     */
    public final static int ORDER_VIRTUAL_STATE_LOCK = 40;

    /**
     * 订单操作-支付
     */
    public final static int ORDER_OPERATE_PAY = 0;

    /**
     * 订单操作-取消
     */
    public final static int ORDER_OPERATE_CANCEL = 1;

    /**
     * 订单操作-发货
     */
    public final static int ORDER_OPERATE_DELIVERY = 2;

    /**
     * 订单操作-修改金额
     */
    public final static int ORDER_OPERATE_CHANGE_AMOUNT = 3;

    /**
     * 根据订单状态返回对应的订单值
     */
    public static String orderStatus(int status) {
        String info = "";
        switch (status) {
            case 0:
                info = "已取消";
                break;
            case 10:
                info = "待付款";
                break;
            case 20:
                info = "待发货";
                break;
            case 30:
                info = "待收货";
                break;
            case 40:
                info = "已完成";
                break;
            case 70:
                info = "售后";
                break;
            default:
                info = "已取消";
                break;
        }
        return info;
    }


    public static void main(String[] args) {
        System.out.println(orderStatus(0));
    }

}
