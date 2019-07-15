package com.framework.loippi.service.alipay;

import com.framework.loippi.entity.PayCommon;

import javax.servlet.http.HttpServletRequest;

public interface Alipayh5Service {

    String prePay(PayCommon payCommon);

    String notifyCheck(HttpServletRequest request, String sn);

}
