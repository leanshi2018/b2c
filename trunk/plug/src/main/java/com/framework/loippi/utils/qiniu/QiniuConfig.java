package com.framework.loippi.utils.qiniu;

import java.util.Map;

/**
 * Created by longbh on 2019/1/8.
 */
public class QiniuConfig {

    public static String APP_ID = "PCxLWek_quRVfBUKAG7f6KmuIrtTNA3VVesFdent";

    public static String APP_SECRET = "Nh8QdkHTzkq0WdqCc6S1Me4pGS1mCChqVSNMVakZ";

    public static String bucket = "member";

    public static String qiniuLink = "http://rdnmall.com/";
    
    public static void initPayConfig(Map<String, Object> dto) {
        QiniuConfig.APP_ID = dto.get("appId") + "";
        QiniuConfig.bucket = dto.get("bucket") + "";
        QiniuConfig.APP_SECRET = dto.get("appSecret") + "";
        QiniuConfig.qiniuLink = dto.get("qiniuLink") + "";
    }

}
