package com.framework.loippi.service.alipay;

import com.framework.loippi.entity.PayCommon;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by neil on 2017/6/22.
 */
public interface AlipayMobileService {

    public String toPay(PayCommon payCommon);

    String notifyCheck(HttpServletRequest request,String sn);
}
