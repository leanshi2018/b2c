package com.framework.loippi.service.union;

import com.framework.loippi.entity.PayCommon;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UnionpayService {

    String prePay(PayCommon payCommon, HttpServletRequest request);

    public Map<String, Object> Unionpayfront(HttpServletRequest request, HttpServletResponse rep);

    public Map<String, Object> Unionpayback(HttpServletRequest request, HttpServletResponse resp);

    String notifyCheck(HttpServletRequest request, String sn);

}
