package com.framework.loippi.consts;

/**
 * Created by Administrator on 2017/11/28.
 */
public class ActivityConsts {

    /**
     * 优惠券类型: 专场券-用于活动
     */
    public static final int COUPON_CLASS_ZHUANGCHANG = 2;

    /**
     * 优惠券类型: 通用券-用于所有商品
     */
    public static final int COUPON_CLASS_TONGYONG = 3;

    /**
     * 优惠券类型: 代金券-用于指定店铺
     */
    public static final int COUPON_CLASS_DAIJIN = 1;

    /**
     * 活动状态  10  未开始
     */
    public static final int ACTIVITY_STATUS_WILL_START = 10;

    /**
     * 活动状态 20 活动中
     */
    public static final int ACTIVITY_STATUS_RUNNING = 20;

    /**
     * 活动状态 30 已结束
     */
    public static final int ACTIVITY_STATUS_FINISH = 30;

    /**
     * 限制类型--1 购买金额
     */
    public static final int LIMIT_TYPE_MONEY = 1;

    /**
     * 限制类型--2购买数量
     */
    public static final int LIMIT_TYPE_QUANTITY = 2;

    /**
     * 限制类型--0无限制
     */
    public static final int LIMIT_TYPE_UNLIMIT = 0;

    /**
     * 1开启
     */
    public static final int AUDIT_STATUS_OPEN = 1;

    /**
     * 0 禁用
     */
    public static final int AUDIT_STATUS_CLOSE = 0;
}
