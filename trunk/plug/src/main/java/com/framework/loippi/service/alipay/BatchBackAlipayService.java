package com.framework.loippi.service.alipay;

import com.framework.loippi.entity.AliPayBathBack;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface BatchBackAlipayService {

    public String toBatchBack(AliPayBathBack aliPayBathBack);

    public Map<String, Object> BatchBack(HttpServletRequest request);

}
