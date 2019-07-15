package com.framework.loippi.param.auths;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Param - 第三方登录
 * 
 * @description 第三方登录
 * @author Loippi team
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthsSocialLoginParam{
	
	/** 第三方唯一id */
	@NotEmpty
	private String openid;
	
	/** 手机号 */
	@NotEmpty
	private String mobile;

	/** 密码 */
	@NotEmpty
	private String password;

	/** 登录类型(1:QQ登录，2:微信登录，3:微博登录) */
	@NotNull
	private Integer type;

	/** 头像 */
	private String avatar;

	/** 昵称 */
	private String nickname;
}
