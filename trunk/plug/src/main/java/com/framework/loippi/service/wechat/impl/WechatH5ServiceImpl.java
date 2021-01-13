package com.framework.loippi.service.wechat.impl;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.framework.loippi.entity.PayCommon;
import com.framework.loippi.entity.SNSUserINfo;
import com.framework.loippi.entity.WeiXinOauth2Token;
import com.framework.loippi.service.PaymentService;
import com.framework.loippi.service.wechat.WechatH5Service;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.wechat.h5.config.WachatContent;
import com.framework.loippi.utils.wechat.h5.pojo.AccessToken;
import com.framework.loippi.utils.wechat.h5.util.WeixinUtil;
import com.framework.loippi.utils.wechat.mobile.config.WXpayConfig;
import com.framework.loippi.utils.wechat.mobile.util.WeixinUtils;
import com.framework.loippi.utils.wechat.mobile.util.component.ResponseHandler;

/**
 * Created by longbh on 2017/8/28.
 */
@Service
@Slf4j
public class WechatH5ServiceImpl implements WechatH5Service {

    @Resource
    private PaymentService paymentService;
    private Long lastTicketUpdate = 0l;
    private String jsTicket = "";

    @PostConstruct
    public void init() {

    }

    @Override
    public String toPay(PayCommon payCommon) {
        Map<String, String> parameters = getParameterMap(payCommon, WachatContent.appidH5, WXpayConfig.MCH_ID, WXpayConfig.API_KEY);
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("<xml>");
        Set<String> objSet = parameters.keySet();
        for (Object key : objSet) {
            if (key == null) {
                continue;
            }
            strBuilder.append("<").append(key.toString()).append(">");
            Object value = parameters.get(key);
            if(value!=null){
                strBuilder.append(value.toString());
            }else{
                strBuilder.append("");
            }
            strBuilder.append("</").append(key.toString()).append(">");
        }
        strBuilder.append("</xml>");
        System.out.println(strBuilder.toString());
        String xml = WeixinUtils.request(WachatContent.unifiedorder, "POST",
                strBuilder.toString()).toString();
        System.out.println("==========================");
        System.out.println("========wechatpay===========");
        System.out.println("==========================");
        System.out.println(xml);
        if (org.apache.commons.lang.StringUtils.isNotEmpty(xml) && xml.indexOf("SUCCESS") != -1) {
            if (xml.indexOf("prepay_id") != -1) {
                String prepayid = WeixinUtils.getJsonValue(xml, "prepay_id");
                Map<String, String> clientMap = makeClientMap(prepayid, WachatContent.appidH5, WXpayConfig.API_KEY, parameters.get("nonce_str"));
                return JacksonUtil.toJson(clientMap);
            }
        }
        return null;
    }

    @Override
    public String notifyCheck(HttpServletRequest request, HttpServletResponse response, String sn) {
        // 创建支付应答对象
        ResponseHandler resHandler = new ResponseHandler(request, response);
        resHandler.setKey(WXpayConfig.API_KEY);
        resHandler.getAllParameters();
        try {
            //签名验证
            if (resHandler.isWechatSign()) {
                //根据反过来支付信息修改订单状态
                String result_code = resHandler.getSmap().get("result_code");// 业务结果
                if (result_code != null && result_code.equals("SUCCESS")) {
                    //订单支付金额
                    String total_fee = resHandler.getSmap().get("total_fee");
                    Float total_fee2 = Float.valueOf(total_fee) / 100;
                    //根据订单号修改订单信息
                    paymentService.updatePayBack(sn, resHandler.getSmap().get("transaction_id"), "mp_weichatpay", total_fee);//修改订单状态
                    return "SUCCESS";
                } else {
                    return "FAIL";
                }
            } else {
                return "FAIL";// 给微信系统发送成功信息，微信系统收到此结果后不再进行后续通知
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "FAIL";// 给微信系统发送成功信息，微信系统收到此结果后不再进行后续通知
        }
    }

    @Override
    public SNSUserINfo getOauth2AccessToken(String Appid, String appSecret, String code) {
        WeiXinOauth2Token wat = null;
        SNSUserINfo snsUserINfo = null;
        String requestUrl = WachatContent.auth2_access_token_url + "appid=" + Appid + "&secret=" + appSecret + "&code=" + code + "&grant_type=authorization_code";
        //获取网页授权凭证
        JSONObject jsonobject = WeixinUtil.httpRequest(requestUrl, "GET", null);
        System.out.print(jsonobject.toString());
        if (jsonobject != null) {
            try {
                wat = new WeiXinOauth2Token();
                wat.setAccessToken(jsonobject.getString("access_token"));//网页授权接口调用凭证
                wat.setExpriesIn(jsonobject.getInt("expires_in"));//access_token接口调用凭证超时时间，单位（秒）
                wat.setRefreshToken(jsonobject.getString("refresh_token"));//用户刷新access_token
                wat.setOpenId(jsonobject.getString("openid"));//用户唯一标识
                wat.setScope(jsonobject.getString("scope"));//用户授权的作用域，使用逗号（,）分隔
            } catch (Exception e) {
                wat = null;
                String errorCode = jsonobject.getString("errcode");
                String errorMsg = jsonobject.getString("errmsg");
                log.error("获取网页授权凭证失败 errcode:{} errmsg:{}", errorCode, errorMsg);
            }
        }
        if (wat != null) {
            snsUserINfo = getSNSUSerInfo(wat.getAccessToken(), wat.getOpenId());
        }
        return snsUserINfo;
    }

    @Override
    public String jsTicket(String Appid, String appSecret) {
        if (System.currentTimeMillis() - lastTicketUpdate < 60000) {
            return jsTicket;
        }

        AccessToken snsUserINfo = getAccessToken(Appid, appSecret);
        if (snsUserINfo != null) {
            return JSApiTIcket(snsUserINfo.getToken());
        }
        return null;
    }

    @Override
    public InputStream getImageFromWeixin(String appid, String appsecret, String mediaId) {
        AccessToken accessToken = getAccessToken(appid, appsecret);
        return getInputStream(mediaId, accessToken.getToken());
    }

    public AccessToken getAccessToken(String appid, String appsecret) {
        AccessToken accessToken = null;
        String requestUrl = WachatContent.getaccesstoken + "appid=" + appid + "&secret=" + appsecret + "&grant_type=client_credential";
        JSONObject jsonObject = WeixinUtil.httpRequest(requestUrl, "GET", null);
        // 如果请求成功
        if (null != jsonObject) {
            try {
                accessToken = new AccessToken();
                accessToken.setToken(jsonObject.getString("access_token"));
                accessToken.setExpiresIn(jsonObject.getInt("expires_in"));
            } catch (JSONException e) {
                accessToken = null;
                // 获取token失败
                log.error("获取token失败 errcode:{} errmsg:{}", jsonObject.getInt("errcode"), jsonObject.getString("errmsg"));
            }
        }
        return accessToken;
    }

    //获取用户信息  openid 用户的唯一标识,access_token 网页授权接口调用凭证
    @SuppressWarnings({"deprecation", "unchecked"})
    public SNSUserINfo getSNSUSerInfo(String accessToken, String openid) {
        SNSUserINfo snsUserInfo = null;
        //拼接链接地址
        String requestUrl = WachatContent.getuserifo + "access_token=" + accessToken + "&openid=" + openid + "&lang=zh_CN";
        JSONObject jsonobject = WeixinUtil.httpRequest(requestUrl, "GET", null);
        System.out.print(jsonobject.toString());
        if (jsonobject != null) {
            try {
                snsUserInfo = new SNSUserINfo();
                snsUserInfo.setOpenId(jsonobject.getString("openid"));//用户的标示
                snsUserInfo.setNickname(jsonobject.getString("nickname"));//昵称
                snsUserInfo.setSex(jsonobject.getInt("sex"));//用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
                snsUserInfo.setCountry(jsonobject.getString("country"));//国家，如中国为CN
                snsUserInfo.setProvince(jsonobject.getString("province"));//用户个人资料填写的省份
                snsUserInfo.setCity(jsonobject.getString("city"));//普通用户个人资料填写的城市
                snsUserInfo.setHeadImgUrl(jsonobject.getString("headimgurl"));//用户头像
                //snsUserInfo.setUnionid(jsonobject.getString("unionid"));//如果 用户绑定用户将公众号绑定到微信开放平台帐号后，才会出现该字段
                //snsUserInfo.setPrivilegeList(JSONArray.toList(jsonobject.getJSONArray(jsonobject.getString("privilegeList")),List.class));
            } catch (Exception e) {
                snsUserInfo = null;
                int errorCode = jsonobject.getInt("errcode");
                String errorMsg = jsonobject.getString("errmsg");
                log.error("获取网页授权凭证失败 errcode:{} errmsg:{}", errorCode, errorMsg);
            }
        }
        return snsUserInfo;
    }

    public Map<String, String> makeClientMap(String prepayid, String appId, String signKey, String nonceStr) {
        TreeMap<String, String> parameterMap = new TreeMap<String, String>();
        parameterMap.put("appId", appId);
        parameterMap.put("timeStamp", WeixinUtils.getTimeStamp());
        parameterMap.put("nonceStr", nonceStr);
        // parameterMap.put("partnerid", pluginConfig.getAttribute("mchId"));
        // parameterMap.put("prepayid", prepayid);
        parameterMap.put("package", "prepay_id=" + prepayid);
        parameterMap.put("signType", "MD5");
        parameterMap.put("paySign",
                WeixinUtils.createPaySign((parameterMap), signKey));
        return parameterMap;
    }

    public Map<String, String> getParameterMap(PayCommon payment, String appId, String mchId, String signKey) {
        HashMap<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("appid", appId);
        parameterMap
                .put("body", org.apache.commons.lang.StringUtils.abbreviate(payment.getBody().replaceAll(
                        "[^0-9a-zA-Z\\u4e00-\\u9fa5 ]", ""), 30));
        parameterMap.put("mch_id", mchId);
        parameterMap.put("out_trade_no", payment.getOutTradeNo());
        parameterMap.put("total_fee",
                payment.getPayAmount().multiply(new BigDecimal(100)).intValue() + "");
        parameterMap.put("notify_url", payment.getNotifyUrl());
        parameterMap.put("spbill_create_ip", "127.0.0.1");
        parameterMap.put("nonce_str", WeixinUtils.createNoncestr());
        parameterMap.put("trade_type", "JSAPI");
        parameterMap.put("openid", payment.getOpenId());
        parameterMap.put("sign", WeixinUtils.sign(WeixinUtils.FormatBizQueryParaMap(parameterMap, false), signKey));
        return parameterMap;
    }

    /**
     * 获取jsapi_ticket
     *
     * @param accessToken
     * @return
     */
    public String JSApiTIcket(String accessToken) {
        int result = 0;
        String jsApiTicket = null;
        //拼装创建菜单Url
        String url = WachatContent.jsapi_ticket_url + accessToken;
        //调用接口获取jsapi_ticket
        JSONObject jsonObject = WeixinUtil.httpRequest(url, "GET", null);
        // 如果请求成功
        if (null != jsonObject) {
            try {
                jsApiTicket = jsonObject.getString("ticket");
                jsTicket = jsApiTicket;
                lastTicketUpdate = System.currentTimeMillis();
            } catch (JSONException e) {
                if (0 != jsonObject.getInt("errcode")) {
                    result = jsonObject.getInt("errcode");
                    log.error("JSAPI_Ticket获取失败 errcode:{} errmsg:{}", jsonObject.getInt("errcode"), jsonObject.getString("errmsg"));
                }
            }
        }
        return jsApiTicket;
    }

    private InputStream getInputStream(String mediaId, String accessToken) {
        System.out.println(mediaId + "----" + accessToken);
        InputStream is = null;
        String url = "http://file.api.weixin.qq.com/cgi-bin/media/get?access_token="
                + accessToken + "&media_id=" + mediaId;
        try {
            URL urlGet = new URL(url);
            HttpURLConnection http = (HttpURLConnection) urlGet
                    .openConnection();
            http.setRequestMethod("GET"); // 必须是get方式请求
            http.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            http.connect();
            // 获取文件转化为byte流
            is = http.getInputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return is;
    }

}
