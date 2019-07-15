package com.framework.loippi.service.wechat.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.framework.loippi.entity.WeiRefund;
import com.framework.loippi.service.TSystemPluginConfigService;
import com.framework.loippi.utils.XmltoMap;
import com.framework.loippi.service.wechat.WechatMobileRefundService;
import com.framework.loippi.utils.wechat.h5.handler.RequestHandler;
import com.framework.loippi.utils.wechat.mobile.ClientCustomSSL;
import com.framework.loippi.utils.wechat.mobile.config.WXpayConfig;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Slf4j
@Service
public class WechatMobileRefundServiceImpl implements WechatMobileRefundService {

    @Resource
    private TSystemPluginConfigService tSystemPluginConfigService;

    @Override
    public Map<String, Object> toRefund(WeiRefund weirefund) {
        WXpayConfig.initPayConfig(tSystemPluginConfigService.readPlug("weixinMobilePaymentPlugin"));
        //api地址：http://mch.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_4
        Map<String, Object> mapxml = new HashMap<String, Object>();
        String out_refund_no = weirefund.getOutrefundno();// 退款单号
        String out_trade_no = weirefund.getOuttradeno();// 订单号
        String total_fee = weirefund.getTotalfee() + "";// 总金额
        String refund_fee = weirefund.getRefundfee() + "";//退款金额
        String nonce_str = System.currentTimeMillis() + "";// 随机字符串
        String appid = WXpayConfig.APP_ID; //微信公众号apid
        String appsecret = WXpayConfig.APP_SECRET; //应用密钥
        String mch_id = WXpayConfig.MCH_ID;  //微信商户id
        String op_user_id = WXpayConfig.MCH_ID;//就是MCHID
        String apikey = WXpayConfig.API_KEY;//商户平台上的那个KEY
        SortedMap<String, String> packageParams = new TreeMap<String, String>();
        packageParams.put("appid", appid);
        packageParams.put("mch_id", mch_id);
        packageParams.put("nonce_str", nonce_str);
        packageParams.put("out_trade_no", out_trade_no);
        packageParams.put("out_refund_no", out_refund_no);
        packageParams.put("total_fee", total_fee);
        packageParams.put("refund_fee", refund_fee);
        packageParams.put("op_user_id", op_user_id);
        RequestHandler reqHandler = new RequestHandler();
        reqHandler.init(appid, appsecret, apikey);
        String sign = reqHandler.createSign(packageParams);
        String xml = "<xml>" + "<appid>" + appid + "</appid>" + "<mch_id>"
                + mch_id + "</mch_id>" + "<nonce_str>" + nonce_str
                + "</nonce_str>" + "<sign><![CDATA[" + sign + "]]></sign>"
                + "<out_trade_no>" + out_trade_no + "</out_trade_no>"
                + "<out_refund_no>" + out_refund_no + "</out_refund_no>"
                + "<total_fee>" + total_fee + "</total_fee>"
                + "<refund_fee>" + refund_fee + "</refund_fee>"
                + "<op_user_id>" + op_user_id + "</op_user_id>" + "</xml>";
        String createOrderURL = "https://api.mch.weixin.qq.com/secapi/pay/refund";
        try {
            ClientCustomSSL clientCustomSSL = new ClientCustomSSL();
            String s = clientCustomSSL.doRefund(createOrderURL, xml);
            mapxml = XmltoMap.doXMLParse(s);//xml解析成相应的map
            System.out.println(mapxml);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapxml;
    }
    
}
