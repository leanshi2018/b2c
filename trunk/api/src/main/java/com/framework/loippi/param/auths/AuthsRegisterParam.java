package com.framework.loippi.param.auths;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Param - 注册用户
 *
 * @author Loippi team
 * @version 2.0
 * @description 注册用户
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthsRegisterParam {

    /**
     * 手机号
     */
    @NotEmpty(message = "手机号不能为空")
    private String mobile;

    /**
     * 密码
     */
    @NotEmpty
    private String password;

    @NotEmpty
    private String name;

    /**
     * 用户类型
     */
    @NotNull
    private Integer registerType;

    /**
     * 第三方注册openid
     */
    private String openid;

    /**
     * 第三方注册类型(0-手机注册,1:QQ注册, 2:微信注册, 3:微博注册)
     */
    private Integer type;

    //=====扩展信息=====
    private String memberTrueId;

    private String memberTrueName;

    //邀请码
    @NotEmpty
    private String invitCode;
    /**
     * 验证码
     */
    @NotEmpty
    private String code;

}
