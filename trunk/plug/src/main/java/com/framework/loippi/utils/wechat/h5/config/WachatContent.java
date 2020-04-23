package com.framework.loippi.utils.wechat.h5.config;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Map;

/**
 * @author ihui
 *         微信公共号配置
 */
public class WachatContent {

    public static final String getuserifo = "https://api.weixin.qq.com/sns/userinfo?";//获取用户信息
    public static final String getaccesstoken = "https://api.weixin.qq.com/cgi-bin/token?";//获取access_token跳转
    public static final String unifiedorder = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    private static final String auth2_code_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid={appid}&redirect_uri={redirect_uri}&response_type=code&scope={scope}&state=STATE#wechat_redirect";
    public static final String auth2_access_token_url = "https://api.weixin.qq.com/sns/oauth2/access_token?";
    //获取JSAPI_Ticket
    public static String jsapi_ticket_url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?type=jsapi&access_token=";
    //public static String appid = "wxd52948077ee73da4";//公众账号ID
    //public static String appsecret = "22e786adfadbb81973d0c36f1e4d3ead";//应用密钥
    public static String appid = "wx6e94bb18bedf3c4c";//公众账号ID
    //public static String appsecret = "a8729cb3fa0b5edbcef7be7cb904e41f";//应用密钥
    public static String appsecret = "c7af91f8b99593a2073f6e691f8ebfc4";//应用密钥
    public static String partner = "1494509422";//微信支付商户号
    public static String apikey = "h7361t2y27shdie8uy6tgh789mnbg654";//API密钥，在商户平台设置

    public static final String AUTH2_SCOPE_USERINFO = "snsapi_userinfo";//

    public static void initPayConfig(Map<String, Object> dto) {
        WachatContent.appid = dto.get("appId") + "";
        WachatContent.appsecret = dto.get("PartnerKey") + "";
        WachatContent.partner = dto.get("seller") + "";
        WachatContent.apikey = dto.get("PaySignKey") + "";
    }

    /**
     * sha1加密
     *
     * @param str
     * @return
     */
    public static String sha1Encrypt(String str) {
        String signature = null;
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(str.getBytes("UTF-8"));
            signature = byteToHex(crypt.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return signature;
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    public static String getRedirectWeixinAuth2SiteUrl(String appid,
                                                       String redirect_uri, String scope) {
        return auth2_code_url.replace("{appid}", appid)
                .replace("{redirect_uri}", redirect_uri)
                .replace("{scope}", scope);
    }

}

