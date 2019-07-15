package com.framework.loippi.service.wechat.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.framework.loippi.entity.PayCommon;
import com.framework.loippi.service.PaymentService;
import com.framework.loippi.service.TSystemPluginConfigService;
import com.framework.loippi.service.wechat.WechatMobileService;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.wechat.mobile.config.WXpayConfig;
import com.framework.loippi.utils.wechat.mobile.util.WeixinUtils;
import com.framework.loippi.utils.wechat.mobile.util.component.ResponseHandler;

import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by neil on 2017/6/22.
 */
@Service
@Slf4j
public class WechatMobileServiceImpl implements WechatMobileService {

    @Resource
    private PaymentService paymentService;
    @Resource
    private TSystemPluginConfigService tSystemPluginConfigService;

    @PostConstruct
    public void init() {
        WXpayConfig.initPayConfig(tSystemPluginConfigService.readPlug("weixinMobilePaymentPlugin"));
    }

    @Override
    public String toPay(PayCommon payCommon) {
        WXpayConfig.initPayConfig(tSystemPluginConfigService.readPlug("weixinMobilePaymentPlugin"));
        Map<String, String> parameters = getParameterMap(payCommon);
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("<xml>");
        Set<String> objSet = parameters.keySet();
        for (Object key : objSet) {
            if (key == null) {
                continue;
            }
            strBuilder.append("<").append(key.toString()).append(">");
            Object value = parameters.get(key);
            strBuilder.append(value.toString());
            strBuilder.append("</").append(key.toString()).append(">");
        }
        strBuilder.append("</xml>");
        String xml = WeixinUtils.request(getRequestUrl(), "POST",
            strBuilder.toString()).toString();
        System.out.println("-------------------------------------------------------");
        System.out.println("--------------------" + strBuilder.toString() + "-----------------------------------");
        System.out.println("-------------------------------------------------------");
        System.out.println("-------------------" + xml + "------------------------------------");
        System.out.println("-------------------------------------------------------");

        if (StringUtils.isNotEmpty(xml) && xml.indexOf("SUCCESS") != -1) {
            if (xml.indexOf("prepay_id") != -1) {
                String prepayid = WeixinUtils.getJsonValue(xml, "prepay_id");
                Map<String, String> clientMap = makeClientMap(prepayid,
                    parameters.get("nonce_str"));
                return JacksonUtil.toJson(clientMap);
            }
        }
        System.out.println("**************************************************");
        System.out.println("**" + xml + "**");
        System.out.println("**************************************************");
        return null;
    }

    public Map<String, String> getParameterMap(PayCommon payCommon) {
        // 这里notify_url是 支付完成后微信发给该链接信息，可以判断会员是否支付成功，改变订单状态等。
        HashMap<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("appid", WXpayConfig.APP_ID);
        parameterMap.put("body", payCommon.getBody());
        parameterMap.put("mch_id", WXpayConfig.MCH_ID);
        parameterMap.put("out_trade_no", payCommon.getOutTradeNo());
        parameterMap.put("total_fee",
            payCommon.getPayAmount().multiply(new BigDecimal(100)).intValue()
                + "");
        parameterMap.put("notify_url", payCommon.getNotifyUrl());
        parameterMap.put("spbill_create_ip", "123.23.11.11");             //request.getRemoteAddr()
        parameterMap.put("nonce_str", WeixinUtils.createNoncestr());
        parameterMap.put("trade_type", "APP");
        parameterMap.put("sign", WeixinUtils.sign(
            WeixinUtils.FormatBizQueryParaMap(parameterMap, false),
            WXpayConfig.API_KEY));
        return parameterMap;
    }

    public String getRequestUrl() {
        return "https://api.mch.weixin.qq.com/pay/unifiedorder";
    }

    public Map<String, String> makeClientMap(String prepayid, String nonceStr) {
        HashMap<String, String> parameterMap = new HashMap<String, String>();
        parameterMap.put("appid", WXpayConfig.APP_ID);
        parameterMap.put("partnerid", WXpayConfig.MCH_ID);
        parameterMap.put("prepayid", prepayid);
        parameterMap.put("package", "Sign=WXPay");
        parameterMap.put("noncestr", nonceStr);
        parameterMap.put("timestamp", WeixinUtils.getTimeStamp());
        parameterMap.put("sign", WeixinUtils.sign(
            WeixinUtils.FormatBizQueryParaMap(parameterMap, false),
            WXpayConfig.API_KEY));
        return parameterMap;
    }

    @Override
    public String notifyCheck(HttpServletRequest request, HttpServletResponse response, String sn) {
        ResponseHandler resHandler = new ResponseHandler(request, response);
        resHandler.setKey(WXpayConfig.API_KEY);
        resHandler.getAllParameters();
        //签名验证
        if (resHandler.isWechatSign()) {
            log.info("是微信V3签名,规则是:按参数名称a-z排序,遇到空值的参数不参加签名");
        } else {
            //如果积分有使用 失败则返回积分
            paymentService.updatePayfailBack(sn);
            log.info("不是 微信V3签名,规则是:按参数名称a-z排序,遇到空值的参数不参加签名");
            return "success";
        }
        String return_code = resHandler.getSmap().get("return_code");// 通信标识
        String return_msg = resHandler.getSmap().get("return_msg");// 通知返回的信息，如非空，为错误，原因：签名失败，参数格式效验失败
        if (!StringUtil.isEmpty(return_code) && "success".toUpperCase().equals(return_code.toUpperCase())) {
            // String sign = resHandler.getSmap().get("sign"); // 签名
            // String nonce_str = resHandler.getSmap().get("nonce_str"); //
            // 随机字符串
            String mch_id = resHandler.getSmap().get("mch_id"); // 商户号
            String appid = resHandler.getSmap().get("appid"); // 公众账号id
            // String device_info = resHandler.getSmap().get("device_info");
            // //
            // 设备号
            String result_code = resHandler.getSmap().get("result_code");// 业务结果
            String err_code = resHandler.getSmap().get("err_code"); // 错误代码
            String err_code_des = resHandler.getSmap().get("err_code_des"); // 错误代码描述
            // log.error("业务结果：" + result_code);
            if (!StringUtil.isEmpty(result_code) && "success".toUpperCase().equals(result_code.toUpperCase())) {
                // 用户标识
                String openid = resHandler.getSmap().get("openid");
                // 是否关注公众账号
                String is_subscribe = resHandler.getSmap().get("is_subscribe");
                // 交易类型
                String trade_type = resHandler.getSmap().get("trade_type");
                // 付款银行
                String bank_type = resHandler.getSmap().get("bank_type");
                // 总金额
                String total_fee = resHandler.getSmap().get("total_fee");
                // 现金券金额
                String coupon_fee = resHandler.getSmap().get("coupon_fee");
                // 货比类型
                String fee_type = resHandler.getSmap().get("fee_type");
                // 微信支付订单号
                String transaction_id = resHandler.getSmap().get("transaction_id");
                // 商户订单号，与请求一致
                String out_trade_no = resHandler.getSmap().get("out_trade_no");
                // 支付完成时间
                String time_end = resHandler.getSmap().get("time_end");

                if (!StringUtil.isAllBlank(mch_id) && !StringUtil.isEmpty(appid) && WXpayConfig.MCH_ID.equals(mch_id)
                    && WXpayConfig.APP_ID.equals(appid)) {// 只处理系统设置商户的异步同志请求
                    log.info("微信支付异步处理成功！");
                    System.out.println("支付成功了！" + out_trade_no);
                    Float total_fee2 = Float.valueOf(total_fee) / 100;
                    paymentService.updatePayBack(out_trade_no, transaction_id, "open_weichatpay",
                        total_fee2.toString());//修改订单状态  开放平台支付
                }
            } else {
                //如果积分有使用 失败则返回积分
                paymentService.updatePayfailBack(sn);
                log.error("交易失败！错误代码(err_code)：" + err_code + " 错误代码描述：" + err_code_des);
            }
            return "success";
        }
        return "fail";
    }

}
