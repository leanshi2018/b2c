package com.framework.loippi.enus;

public enum UserLoginType {

	MOBILE(0, "手机登录"),
	QQ(1, "QQ登录"),
	WECHAT(2, "微信登录"),
	WEIBO(3,"微博登录");

	public final int code;
	public final String alias;

	private UserLoginType(int code, String alias) {
		this.code = code;
		this.alias = alias;
	}

	public static UserLoginType of(int code) {
		for (UserLoginType status : values()) {
			if (code == status.code) {
				return status;
			}
		}
		return MOBILE;
	}
}
