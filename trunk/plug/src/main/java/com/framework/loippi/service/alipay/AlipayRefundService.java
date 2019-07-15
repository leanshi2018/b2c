package com.framework.loippi.service.alipay;

import com.framework.loippi.entity.AliPayRefund;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface AlipayRefundService {

    public String toRefund(AliPayRefund aliPayRefund);

    public Map<String, Object> Refundback(HttpServletRequest request);

    //提现
  //  public boolean transferMoeny(AlipayFundTransToaccountTransferModel model);

}
