package com.framework.loippi.service.wechat;

import com.framework.loippi.entity.WeiRefund;

import java.util.Map;

public interface WechatMobileRefundService {
    //跳转到微信退款页
    public Map<String, Object> toRefund(WeiRefund weirefund);

}