package com.framework.loippi.utils;

public enum APIXerror {
    SYSTEM_ERROR(5000000, "系统出错"),
    USER_SESS_EXPIRED(5000103, "会话信息失效"),
    USER_NOT_LOGIN(5000104, "用户未登录"),
    LOGIN_PASSWORD_ERROR(5000105, "用户账号或密码错误"),
    OBJECT_IS_NOT_EXIST(5000106, "用户不存在"),
    LOGIN_ACCOUNT_DISABLED(5000107, "用户状态为禁用"),
    PARAM_INVALID(5000108, "参数有误"),
    USER_UNBIND(5000109, "用户未绑定"),
    MOBILE_INUSER(5000111, "该手机已被注册"),
    VALID_CODE_ERROR(5000112, "验证码有误"),
    NOT_MOBILE(5000113, "该号码非手机号"),
    CODE_IS_NOT_EXIST(5000114, "验证码不存在"),
    OBJECT_IS_NOT_MUSICIAN(5000115, "用户未实名认证"),
    OBJECT_IS_NOT_VERIFICATION(5000116, "用户未经过认证"),
    OBJECT_IS_EXIST(5000117, "用户已经存在"),
    OLD_PASSWORD_ERROR(5000118, "用户旧密码错误"),
    OLDNEW_PASSWORD_SAME(5000119, "用户新密码和旧密码一样"),

    Wallet_IS_NOT_EXIST(5001000, "用户钱包不存在"),

    //用戶與音樂人端登錄
    USER_PENDING(5000113, "暫未完成音樂人實名認證"),
    USER_ONAGREE(5000114, "您的實名認證信息正在審核中"),
    USER_DISAGREE(5000115, "您的實名認證信息審核失敗");

    public Integer xCode;
    public String massage;

    APIXerror(Integer xCode, String massage) {
        this.xCode = xCode;
        this.massage = massage;
    }


    /**
     * 不能为空
     */
    public static final int NOT_NULL = 5000002;
    /**
     * 长度太小
     */
    public static final int SIZE_TOO_MIN = 5000507;

}
