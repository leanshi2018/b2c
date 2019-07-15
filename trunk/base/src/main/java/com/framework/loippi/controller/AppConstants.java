package com.framework.loippi.controller;

/**
 * Created by longbh on 16/9/5.
 */
public class AppConstants {

    /**
     * 成功
     */
    public static final int OK = 2000000;

    /**
     * 通用失败
     */
    public static final int FAIL = 5000000;
    /**
     * 找不到
     */
    public static final int NOT_FOUND = 5000001;

    /**
     * 不能为空
     */
    public static final int NOT_NULL = 5000002;
    /**
     * 用户会话失效
     */
    public static final int USER_SESS_EXPIRED = 5000103;

    /**
     * 用户未登录
     */
    public static final int USER_NOT_LOGIN = 5000104;


    /**
     * 帐号或密码错误
     */
    public static final int USER_NOT_INFO = 5000105;

    /**
     * 帐号不存在
     */
    public static final int ACCOUNT_NOT_EXIST = 5000106;


    /**
     * 验证码错误
     */
    public static final int CAPTCHA__NOT_EXIST = 5000107;

    /**
     * 参数不完整
     */
    public static final int PARAMETER__NOT_COMPLETE = 5000108;

    /**
     * 用户已存在
     */
    public static final int USER_EXIST = 5000109;
    /**
     * 用户已经被禁用
     */
    public static final int USER_NOG_EXIST = 5000504;

    /**
     * 用户已经被禁用
     */
    public static final int LOCK_USER = 5000504;


    /**
     * 商品不存在
     */
    public static final int GOODS_NOT_EXIST = 5000701;


    /**
     * 取消订单失败
     */
    public static final int ORDER_CANCEL_ERROR = 5000702;


    /**
     * 手机号码已使用
     */
    public static final int NEW_PHONE_EXIST = 5000703;

    /**
     * 限制购买数量
     */
    public static final int LIMIT_BUY_QUANTITY = 5000702;

    /**
     * 库存不足
     */
    public static final int GOODS_NO_MORE = 5000703;


    public static final int MEMBER_POINTS_NO = 5000704;//积分不足


    /**
     * 企业
     */
    public static final int COMPANY_NOT_NULL = 5000801;
    /**
     * 错误的验证码
     */
    public static final int OLD_ERROR_CAPTCHA = 5000802;
    //余额不住
    public static final int NOT_SOMUTCH = 5000803;
    //积分不住
    public static final int NOT_POINT = 5000804;

    /**
     * 商品已下架
     */
    public static final int GOODS_NOT_SHOW = 6000001;

    /**
     * 规格不存在
     */
    public static final int GOODS_SPEC_NOT_EXISTS = 6000001;

    /**
     * 商品审核中
     */
    public static final int GOODS_IN_AUDIT = 6000002;

    /**
     * 商品已删除
     */
    public static final int GOODS_IS_DEL = 6000003;

    /**
     * 商品状态异常
     */
    public static final int GOODS_STATE_ERRO = 6000004;

    /**
     * 价格变动
     */
    public static final int GOODS_PRICE_CHANGE = 6000005;
    /**
     * 重量变动
     */
    public static final int GOODS_WEIGHT_CHANGE = 6000013;
    /**
     * pv变动
     */
    public static final int GOODS_PPV_CHANGE = 6000014;

    /**
     * 优惠券不满足使用条件
     */
    public static final int COUPON_UNAVAILABLE = 6000006;

    /**
     * 订单已锁定
     */
    public static final int ORDER_LOCKED = 6000007;

    /**
     * 团购订单不能取消
     */
    public static final int ORDER_CANCEL_FAIL = 6000008;

    /**
     * 收货地址不存在
     */
    public static final int RECEIVED_ADDRESS_NOT_EXIT = 6000009;

    /**
     * 已经提醒发货
     */
    public static final int HAD_REMIND_DELIVERY = 6000010;

    /**
     * 订单更新失败
     */
    public static final int ORDER_UPDATE_FAIL = 6000011;

    /**
     * 评价包含敏感词
     */
    public static final int EVALUATION_CONTAIN_SENSITIVE_WORD = 6000012;
}
