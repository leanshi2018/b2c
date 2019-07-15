package com.framework.loippi.utils;

public class Xerror {
    /**
     * 非法操作
     */
    public static final int SYSTEM_ILLEGALITY = 10000;

    /**
     * 系统错误
     */
    public static final int SYSTEM_ERROR = 11000;

    /**
     * 验证码失效或者有误
     */
    public static final int VALID_CODE_ERROR = 10001;

    /**
     * 此账户不存在
     */
    public static final int OBJECT_IS_NOT_EXIST = 10002;

    /**
     * 参数错误
     */
    public static final int PARAM_INVALID = 10003;

    //地址不存在
    public static final int USER_ADDRESS_NOT_EXIST = 83002;

    /**
     *
     */
    public static final int OBJECT_IS_EXIST = 10004;

    /**
     * APP暂无更新
     */
    public static final int VERSION_IS_NOT_UPDATE = 20001;

    /**
     * 登录密码错误
     */
    public static final int LOGIN_PASSWORD_ERROR = 30001;

    /**
     * 账号锁定
     */
    public static final int LOGIN_ACCOUNT_DISABLED = 30002;

    /**
     * 验证码错误
     */
    public static final int LOGIN_VALIDCODE_ERROR = 30003;

    /**
     * 用户未登录代码
     */
    public static final int USER_UNLOGIN_JSON_CODE = 40001;

    /**
     * 网址URL不存在的代码
     */
    public static final int URL_NOFOUND_JSON_CODE = 40002;

    /**
     * 请求方法错误, 需要用POST
     */
    public static final int URL_METHOD_NOPOST_JSON_CODE = 40003;

    /**
     * 退出登录异常
     */
    public static final int LOGIN_OUT_EXCEPTION_ERROR = 40004;

    /**
     * 登录缓存写入失败
     */
    public static final int LOGIN_SET_CACHE_ERROR = 40005;

    /**
     * 订单处理失败，请重试
     */
    public static final int ORDER_HANDLE_FAILURE_ERROR = 50001;

    /**
     *
     */
    public static final int REDUCE_STOCKORDER_FAILURE = 60002;

    /**
     * 你还没提交验证
     */
    public static final int RELNAME_IS_NOT_EXIST = 40006;

    /**
     * 您没有店铺！
     */
    public static final int STORE_IS_NOT_EXIST = 40007;
}
