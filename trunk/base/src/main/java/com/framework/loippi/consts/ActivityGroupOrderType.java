package com.framework.loippi.consts;

/**
 * 参团订单状态： 0参团失败 1正在参团   2 团已满    3参团失败订单已退款  4待支付后，开团或参团',
 */
public class ActivityGroupOrderType {

    /**
     * 0参团失败
     */
    public final static int ACTIVITY_GROUP_ORDER_TYPE_FAIL = 0;


    /**
     *  1正在参团
     */
    public final static int ACTIVITY_GROUP_ORDER_TYPE_NORMAL = 1;

    /**
     * 2 团已满
     */
    public final static int ACTIVITY_GROUP_ORDER_TYPE_SUCCESS  = 2;


    /**
     *  3参团失败订单已退款
     */
    public final static int ACTIVITY_GROUP_ORDER_TYPE_FAIL_REFUND = 3;


    /**
     *  4待支付后，开团或参团
     */
    public final static int ACTIVITY_GROUP_ORDER_TYPE_FAIL_STAY = 4;

    /**
     *  5  在处理退款过程中，发生异常
     */
    public final static int ACTIVITY_GROUP_ORDER_TYPE_EXCEPTION_STAY = 5;

    /**
     *  6  活动已缺失
     */
    public final static int ACTIVITY_GROUP_ORDER_TYPE_INVALID_STAY = 6;

}
