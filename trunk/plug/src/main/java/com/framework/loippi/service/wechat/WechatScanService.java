package com.framework.loippi.service.wechat;

import com.framework.loippi.entity.PayCommon;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 微信扫描二维码支付
 *
 * @author ihui
 */
public interface WechatScanService {

    //生成二维码
    String prePay(PayCommon payCommon);

    String notifyCheck(HttpServletRequest request, HttpServletResponse response, String sn);

}
