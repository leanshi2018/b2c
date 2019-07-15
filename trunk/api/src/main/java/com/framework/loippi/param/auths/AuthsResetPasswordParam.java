package com.framework.loippi.param.auths;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * Param - 忘记密码
 * 
 * @description 忘记密码
 * @author Loippi team
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthsResetPasswordParam {

	/** 手机号 */
	@NotNull
	private String mobile;

	/** 新密码 */
	@NotNull
	private String password;

	/**
	 * 验证码
	 */
	@NotNull
	private String code;

}
