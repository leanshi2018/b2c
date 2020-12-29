package com.framework.loippi.enus;

public enum VerifyCodeType {

	REGISTER(0, "注册"),
	RESETPWD(1, "忘记密码"),
	VERIFY_MOBILE(2, "验证手机号"),
	RESET_MOBILE(3, "修改手机号"),
	PC_LOGIN(4,"pc端登录"),
	NONINDUCTIVE_REGISTER(6,"无感用户注册获取验证码"),
	BankCards_MOBILE(5, "银行卡验证"),;
	public final int code;
	public final String alias;

	private VerifyCodeType(int code, String alias) {
		this.code = code;
		this.alias = alias;
	}

	public static VerifyCodeType of(int code) {
		for (VerifyCodeType type : values()) {
			if (code == type.code) {
				return type;
			}
		}
		return REGISTER;
	}
}
