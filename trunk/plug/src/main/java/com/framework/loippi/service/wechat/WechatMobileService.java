package com.framework.loippi.service.wechat;

import com.framework.loippi.entity.PayCommon;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by neil on 2017/6/22.
 */
public interface WechatMobileService {

    String toPay(PayCommon payCommon);

    String notifyCheck(HttpServletRequest request, HttpServletResponse response, String sn);

}
