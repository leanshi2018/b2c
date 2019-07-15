package com.framework.loippi.service;

import com.framework.loippi.entity.PayCommon;

import javax.servlet.http.HttpServletRequest;

public interface BillService {

    String prePay(PayCommon payCommon);

    String notifyCheck(HttpServletRequest request,String sn);

}
