package com.framework.loippi.entity.walet;

/**
 * Created by Administrator on 2017/10/9.
 */
public enum LgTypeEnum {

    RED_PACKET("red_packet"),//拼手气红包
    RED_PACKET_REGISTER("red_packet_register"),//注册红包
    RECOMMEND_REBATE("recommend_rebate"),//推荐返佣
    GOODS_EVALUATE("goods_evaluate"),//评价返佣
    // 订单支付
    ORDER_PAY("order_pay"),//订单支付
    // 订单取消
    ORDER_CANCEL("order_cancel"),//取消订单
    // 订单退款
    ORDER_RETURN_REFUND("order_return_refund"),
    // 订单支付
    OFFLINE_STORE_PAY("offline_store_pay"),
    // 充值
    RECHARGE("RECHARGE"),
    // 推荐商品佣金
    GOODS_COMMISSION("goods_commission"),
    // 提现手续费
    CASH_APPLY_SERVICEAMOUNT("cash_apply_serviceAmount"),
    // 提现审核成功
    CASH_PAY("cash_pay"),
    // 提现审核失败
    CASH_DEL("cash_del"),
    // 申请提现
    CASH_APPLY("cash_apply");


    public String value;

    LgTypeEnum(String value) {
        this.value = value;
    }

}
