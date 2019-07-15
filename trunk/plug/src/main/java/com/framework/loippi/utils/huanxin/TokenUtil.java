package com.framework.loippi.utils.huanxin;


import com.framework.loippi.utils.JacksonUtil;
import io.swagger.client.ApiException;
import io.swagger.client.api.AuthenticationApi;
import io.swagger.client.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

/**
 * Created by longbh on 2019/1/28.
 */
public class TokenUtil {

    public static String GRANT_TYPE;
    private static String ORG_NAME;
    private static String APP_NAME;

    private static String CLIENT_ID;
    private static String CLIENT_SECRET;
    private static Token BODY;
    private static AuthenticationApi API = new AuthenticationApi();
    private static String ACCESS_TOKEN;
    private static Long EXPIREDAT = -1L;
    private static final Logger logger = LoggerFactory.getLogger(TokenUtil.class);

    /**
     * get token from server
     */
    public static void instance(String type, String client, String secret, String orgName, String appName) {
        GRANT_TYPE = type;
        CLIENT_ID = client;
        CLIENT_SECRET = secret;
        ORG_NAME = orgName;
        APP_NAME = appName;
        BODY = new Token().clientId(CLIENT_ID).grantType(GRANT_TYPE).clientSecret(CLIENT_SECRET);
    }

    public static void initTokenByProp() {
        String resp = null;
        try {
            resp = API.orgNameAppNameTokenPost(ORG_NAME, APP_NAME, BODY);
        } catch (ApiException e) {
            logger.error(e.getMessage());
        }
        Map map = JacksonUtil.convertMap(resp);
        ACCESS_TOKEN = " Bearer " + map.get("access_token");
        EXPIREDAT = System.currentTimeMillis() + (Integer) map.get("expires_in");

    }

    /**
     * get Token from memory
     *
     * @return
     */
    public static String getAccessToken() {
        if (ACCESS_TOKEN == null || isExpired()) {
            initTokenByProp();
        }
        return ACCESS_TOKEN;
    }

    private static Boolean isExpired() {
        return System.currentTimeMillis() > EXPIREDAT;
    }

}
