package com.framework.loippi.param.auths;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * Param - 无感注册用户
 *
 * @author zc
 * @description 无感注册用户注册传递参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthsNoninductiveRegisterParam {

    /**
     * 手机号
     */
    @NotEmpty(message = "手机号不能为空")
    private String mobile;

    //邀请码
    private String invitCode;

    //验证码
    private String code;

    //验证类型 1本机验证 2验证码验证，如果为验证码验证，则验证码不可以为空
    @NotNull
    @Min(1)
    @Max(2)
    private Integer validationType;
}
