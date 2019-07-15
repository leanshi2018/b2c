package com.framework.loippi.service.wechat;

import com.framework.loippi.entity.PayCommon;
import com.framework.loippi.entity.SNSUserINfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * Created by longbh on 2017/8/28.
 */
public interface WechatH5Service {

    String toPay(PayCommon payCommon);

    String notifyCheck(HttpServletRequest request, HttpServletResponse response, String sn);

    //获取网页授权信息 appid 公众账号唯一标示 ，appsecret 公众账号的秘钥
    public SNSUserINfo getOauth2AccessToken(String Appid, String appSecret, String code);

    public String jsTicket(String Appid, String appSecret);

    InputStream getImageFromWeixin(String appid, String appsecret, String mediaId);
}
