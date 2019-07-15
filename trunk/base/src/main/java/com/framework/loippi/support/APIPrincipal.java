package com.framework.loippi.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class APIPrincipal {


    public static final String PREFIX_SITE_SESSION = "APP_SITE_SESSION_";

    public static final String PREFIX_USER_SESSION = "APP_API_SESSION_";

    public static final String PORPERTITY_AUTHENTICATION = "authentication";


    /**
     * 鉴权码
     */
    private String authentication;

    /**
     * 账户编号
     */
    private Long id;

    /**
     * 账户
     */
    private String account;

    private String nickName;

    private String openId;

    private String unionid;

    public static String getCacheKey(String authentication) {
        return PREFIX_USER_SESSION + authentication;
    }

}
