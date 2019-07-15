package com.framework.loippi.service.alipay.impl;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;
import com.framework.loippi.entity.PayCommon;

import com.framework.loippi.service.PaymentService;
import com.framework.loippi.service.TSystemPluginConfigService;
import com.framework.loippi.service.alipay.AlipayMobileService;
import com.framework.loippi.utils.alipay.mobile.config.AlipayConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by neil on 2017/6/22.
 */
@Service
@Slf4j
public class AlipayMobileServiceImpl implements AlipayMobileService {

    @Resource
    private PaymentService paymentService;
    @Resource
    private TSystemPluginConfigService tSystemPluginConfigService;

    @PostConstruct
    public void init() {
        AlipayConfig.initAlipayConfig(tSystemPluginConfigService.readPlug("alipayMobilePaymentPlugin"));
    }

    @Override
    public String toPay(PayCommon payCommon) {
                AlipayConfig.initAlipayConfig(tSystemPluginConfigService.readPlug("alipayMobilePaymentPlugin"));
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.gatewayUrl, AlipayConfig.APPID, AlipayConfig.private_key, AlipayConfig.format, AlipayConfig.input_charset, AlipayConfig.ali_public_key, AlipayConfig.sign_type);
        //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.trade.app.pay
        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        //SDK已经封装掉了公共参数，这里只需要传入业务参数。以下方法为sdk的model入参方式(model和biz_content同时存在的情况下取biz_content)。
        AlipayTradeAppPayModel model = new AlipayTradeAppPayModel();
        model.setBody(payCommon.getBody());
        model.setSubject(payCommon.getTitle());
        //请保证OutTradeNo值每次保证唯一
        model.setOutTradeNo(payCommon.getOutTradeNo());
        model.setTimeoutExpress("30m");
        model.setTotalAmount(payCommon.getPayAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
        model.setProductCode("QUICK_MSECURITY_PAY");
        request.setBizModel(model);
        request.setNotifyUrl(payCommon.getNotifyUrl());
        try {
            //这里和普通的接口调用不同，使用的是sdkExecute
            AlipayTradeAppPayResponse response = alipayClient.sdkExecute(request);
            //就是orderString 可以直接给客户端请求，无需再做处理。
            System.out.println(response.getBody());
            return response.getBody();
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }

        return "";
//        return SignUtils.FormatBizQueryParaMap(getParameterMap(payCommon), false);
//        AlipayConfig.initAlipayConfig(tSystemPluginConfigService.readPlug("alipayMobilePaymentPlugin"));
//        return SignUtils.FormatBizQueryParaMap(getParameterMap(payCommon), false);
    }

    @Override
    public String notifyCheck(HttpServletRequest request, String sn) {
        // 获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        @SuppressWarnings("rawtypes")
        Map requestParams = request.getParameterMap();
        for (@SuppressWarnings("unchecked")
             Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"),
            // "gbk");
            params.put(name, valueStr);
        }

        // 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)
        // 商户订单号
        String out_trade_no_str = request.getParameter("out_trade_no");
        System.out.println("--------------------------------------------------------------");
        System.out.println("--------------trade_no_str--" + out_trade_no_str + "--------------");
        System.out.println("--------------------------------------------------------------");
        String trade_no_str = request.getParameter("trade_no");
        String trade_status_str = request.getParameter("trade_status");
        Timestamp nowTime = new Timestamp(new Date().getTime());
        System.out.println("--------------------------------------------------------------");
        System.out.println(nowTime + "接收到支付宝异步通知:out_trade_no:" + out_trade_no_str + " trade_no：" + trade_no_str + " trade_status_str:" + trade_status_str);
        System.out.println("--------------------------------------------------------------");
        log.info(nowTime + "接收到支付宝异步通知:out_trade_no:" + out_trade_no_str + " trade_no：" + trade_no_str + " trade_status_str:" + trade_status_str);
        if (StringUtils.isEmpty(out_trade_no_str)) {
            // 验证失败
            //如果积分有使用 失败则返回积分
            paymentService.updatePayfailBack(sn);
            return "success";
        }
        // 支付宝交易号
        if (StringUtils.isEmpty(trade_no_str)) {
            // 验证失败
            //如果积分有使用 失败则返回积分
            paymentService.updatePayfailBack(sn);
            return "success";
        }
        // 交易状态
        if (StringUtils.isEmpty(trade_status_str)) {
            // 验证失败
            //如果积分有使用 失败则返回积分
            paymentService.updatePayfailBack(sn);
            return "success";
        }
        String out_trade_no = out_trade_no_str;// new String(out_trade_no_str.getBytes("ISO-8859-1") , "UTF-8");
        String trade_no = trade_no_str;//new String(trade_no_str.getBytes("ISO-8859-1") , "UTF-8");
        String trade_status = trade_status_str;//new String(trade_status_str.getBytes("ISO-8859-1") , "UTF-8");
        //String total_fee = request.getParameter("total_fee");
        //String gmt_paymen = request.getParameter("gmt_create");
        System.out.println("--------------------------------------------------------------");
        System.out.println("out_trade_no" + out_trade_no + "trade_no" + trade_no + " trade_status" + trade_status);
        System.out.println("--------------------------------------------------------------");
        // 获取支付宝的通知返回参数
        if (trade_status.equals("TRADE_SUCCESS")) {
            String total_fee = request.getParameter("total_fee");
            paymentService.updatePayBack(out_trade_no, trade_no, "alipay", total_fee);//修改订单状态
        }
        return "success";
    }

    //public Map<String, String> getParameterMap(PayCommon payCommon) {
    //    HashMap<String, String> parameterMap = new HashMap<String, String>();
    //    parameterMap.put("partner", AlipayConfig.partner);
    //    parameterMap.put("seller_id", AlipayConfig.SELLERID);
    //    parameterMap.put("out_trade_no", payCommon.getOutTradeNo());
    //    parameterMap.put("subject", payCommon.getTitle());
    //    parameterMap.put("body", payCommon.getBody());
    //    parameterMap.put("total_fee", payCommon.getPayAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
    //    parameterMap.put("notify_url", payCommon.getNotifyUrl());
    //    parameterMap.put("service", "mobile.securitypay.pay");
    //    parameterMap.put("payment_type", "1");
    //    parameterMap.put("_input_charset", "utf-8");
    //    parameterMap.put("it_b_pay", "30m");
    //    parameterMap.put("return_url", "m.alipay.com");
    //    try {
    //        parameterMap.put("sign", URLEncoder.encode(sign(parameterMap, AlipayConfig.private_key), "utf-8"));
    //    } catch (UnsupportedEncodingException e) {
    //        e.printStackTrace();
    //    }
    //    parameterMap.put("sign_type", "RSA2");
    //    return parameterMap;
    //}

    //private String sign(HashMap<String, String> parameterMap, String key) {
    //    String unSignParaString = SignUtils.FormatBizQueryParaMap(parameterMap, false);
    //    return SignUtils.sign(unSignParaString);
    //}

}
