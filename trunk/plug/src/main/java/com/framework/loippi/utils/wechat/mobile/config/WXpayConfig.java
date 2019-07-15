/**
 *
 */
package com.framework.loippi.utils.wechat.mobile.config;

import java.util.Map;

/**
 * 微信支付参数配置
 *
 * @DateTime 2015-3-4 下午2:12:30
 */
public class WXpayConfig {

    /**
     * appid
     */
    public static String APP_ID = "wxb9149ca7f0636c23";

    public static String APP_SECRET = "53f562875c2f75a9611142604176993b";

    /**
     * 商户号
     */
    public static String MCH_ID = "1539827141";

    /**
     * API密钥，在商户平台设置
     */
    public static String API_KEY = "x1TCZ2frT7AbrgJJRYfd2IoKCWW9tjnx";

    public static void initPayConfig(Map<String, Object> dto) {
        WXpayConfig.APP_ID = dto.get("appId") + "";
        WXpayConfig.MCH_ID = dto.get("seller") + "";
        WXpayConfig.API_KEY = dto.get("PaySignKey") + "";
        WXpayConfig.APP_SECRET = dto.get("PartnerKey") + "";
    }
}
