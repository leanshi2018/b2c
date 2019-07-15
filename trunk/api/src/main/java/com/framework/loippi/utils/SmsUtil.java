package com.framework.loippi.utils;

//import com.aliyuncs.DefaultAcsClient;
//import com.aliyuncs.IAcsClient;
//import com.aliyuncs.exceptions.ClientException;
//import com.aliyuncs.http.MethodType;
//import com.aliyuncs.profile.DefaultProfile;
//import com.aliyuncs.profile.IClientProfile;
import com.framework.loippi.service.RedisService;
//import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
//import org.apache.commons.httpclient.HttpClient;
//import org.apache.commons.httpclient.NameValuePair;
//import org.apache.commons.httpclient.methods.PostMethod;
//import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.RandomStringUtils;

import java.security.MessageDigest;
import java.util.Map;

/**
 * Created by Administrator on 2017/6/19.
 */
public class SmsUtil {

    //public static Map<String, Object> sendCode(String mobiole, String code) throws Exception {
    //    HttpClient httpClient = new HttpClient();
    //    PostMethod postMethod = new PostMethod("http://api.1cloudsp.com/api/send");
    //    postMethod.getParams().setContentCharset("UTF-8");
    //    postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
    //            new DefaultHttpMethodRetryHandler());
    //
    //    String accesskey = "LTAIGcf6P6DcKY6e"; //用户开发key
    //    String accessScrect = "EWjk34z36uIpxY1bbPQWguuVDwd55l"; //用户开发秘钥
    //    String random = RandomStringUtils.random(10); //随机字符串
    //    String timestamp = "" + System.currentTimeMillis(); //当前时间戳
    //    String token = MD5(accessScrect + random + timestamp); //计算token
    //    NameValuePair[] data = {
    //            new NameValuePair("token", token),
    //            new NameValuePair("accesskey", accesskey),
    //            new NameValuePair("timestamp", timestamp),
    //            new NameValuePair("random", random),
    //            new NameValuePair("mobile", mobiole),
    //            new NameValuePair("content", "您正在申请手机注册，验证码为：" + code + "，5分钟内有效！（蜗米商城）"),
    //            new NameValuePair("sign", "蜗米商城"),
    //            new NameValuePair("scheduleSendTime", ""),
    //    };
    //    postMethod.setRequestBody(data);
    //    int statusCode = httpClient.executeMethod(postMethod);
    //    return JacksonUtil.convertMap((String) postMethod.getResponseBodyAsString());
    //}
    //
    //private static String MD5(String value) throws Exception {
    //    StringBuffer md5StrBuff = new StringBuffer();
    //
    //    MessageDigest md5 = MessageDigest.getInstance("MD5");
    //    md5.update(value.getBytes("UTF-8"));
    //
    //    byte[] result = md5.digest();
    //
    //    for (int i = 0; i < result.length; i++) {
    //        if (Integer.toHexString(0xFF & result[i]).length() == 1) {
    //            md5StrBuff.append("0").append(
    //                    Integer.toHexString(0xFF & result[i]));
    //        } else {
    //            md5StrBuff.append(Integer.toHexString(0xFF & result[i]));
    //        }
    //    }
    //    return md5StrBuff.toString();
    //}
    //public static Map<String, Object> sendBatchSms(String mobiole, String code,String signName)throws Exception {
    //    HttpClient httpClient = new HttpClient();
    //    PostMethod postMethod = new PostMethod("http://api.1cloudsp.com/api/send");
    //    postMethod.getParams().setContentCharset("UTF-8");
    //    postMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
    //        new DefaultHttpMethodRetryHandler());
    //
    //    String accesskey = "LTAIGcf6P6DcKY6e"; //用户开发key
    //    String accessScrect = "EWjk34z36uIpxY1bbPQWguuVDwd55l"; //用户开发秘钥
    //    String random = RandomStringUtils.random(10); //随机字符串
    //    String timestamp = "" + System.currentTimeMillis(); //当前时间戳
    //    String token = MD5(accessScrect + random + timestamp); //计算token
    //    NameValuePair[] data = {
    //        new NameValuePair("token", token),
    //        new NameValuePair("accesskey", accesskey),
    //        new NameValuePair("timestamp", timestamp),
    //        new NameValuePair("random", random),
    //        new NameValuePair("mobile", mobiole),
    //        new NameValuePair("content", "您正在申请手机注册，验证码为：" + code + "，5分钟内有效！（蜗米商城）"),
    //        new NameValuePair("sign", signName),
    //        new NameValuePair("scheduleSendTime", ""),
    //    };
    //    postMethod.setRequestBody(data);
    //    int statusCode = httpClient.executeMethod(postMethod);
    //    return JacksonUtil.convertMap((String) postMethod.getResponseBodyAsString());
    //}
    /**
     * 判断验证码是否正确
     *
     * @param mobile
     * @param code
     * @return
     */
    public static boolean valicodeIstrue(String mobile, String code, RedisService redisService) {
        String obj = redisService.get(mobile, String.class);
        if (obj == null || !obj.toString().equals(code)) {
            return false;
        }
        redisService.delete(mobile);
        return true;
    }

    /**
     * 判断验证码是否正确    但不删除验证码
     *
     * @param mobile
     * @param code
     * @return
     */
    public static boolean validMsg(String mobile, String code, RedisService redisService) {
        String obj = redisService.get(mobile, String.class);
        if (obj == null || !obj.toString().equals(code)) {
            return false;
        }
        return true;
    }
}
