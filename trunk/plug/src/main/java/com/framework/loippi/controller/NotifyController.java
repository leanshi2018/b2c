package com.framework.loippi.controller;

import com.framework.loippi.dto.WxJSApiResult;
import com.framework.loippi.service.*;
import com.framework.loippi.service.alipay.AlipayInternaService;
import com.framework.loippi.service.alipay.AlipayMobileService;
import com.framework.loippi.service.alipay.Alipayh5Service;
import com.framework.loippi.service.union.UnionpayService;
import com.framework.loippi.service.wechat.WechatH5Service;
import com.framework.loippi.service.wechat.WechatMobileService;
import com.framework.loippi.service.wechat.WechatScanService;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.wechat.h5.config.WachatContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by longbh on 2017/8/5.
 */
@Slf4j
@Controller
@RequestMapping("/api/paynotify")
public class NotifyController {

    @Resource
    private AlipayMobileService alipayMobileService;
    @Resource
    private Alipayh5Service alipayh5Service;
    @Resource
    private UnionpayService unionpayService;
    @Resource
    private WechatMobileService wechatMobileService;
    @Resource
    private AlipayInternaService alipayInternaService;
    @Resource
    private BillService billService;
    @Resource
    private WechatScanService wechatScanService;
    @Resource
    private WechatH5Service wechatH5Service;
    @Resource
    private TSystemPluginConfigService tSystemPluginConfigService;

    /**
     * 支付回调
     */
    @RequestMapping({"/notifyMobile/{pluginId}/{sn}.json"})
    public void pluginMobileNotify(HttpServletRequest request,
        @PathVariable String pluginId,
        @PathVariable String sn,
        HttpServletResponse response) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            String result = "failure";
            if ("alipayMobilePaymentPlugin".equals(pluginId)) {//阿里手机支付
                result = alipayMobileService.notifyCheck(request, sn);
            } else if ("unionpayMobilePaymentPlugin".equals(pluginId)) {//银联手机支付
                result = unionpayService.notifyCheck(request, sn);
            } else if ("weixinMobilePaymentPlugin".equals(pluginId)) {//微信手机支付
                result = wechatMobileService.notifyCheck(request, response, sn);
            } else if ("weixinInternaPaymentPlugin".equals(pluginId)) {//微信国际支付
                //result = wechatMobileService.notifyCheck(request);
            } else if ("alipayInternaPaymentPlugin".equals(pluginId)) {//alip国际支付
                result = alipayInternaService.notifyCheck(request, sn);
            } else if ("quickBillPaymentPlugin".equals(pluginId)) {//块钱
                result = billService.notifyCheck(request, sn);
            } else if ("alipayH5PaymentPlugin".equals(pluginId)) {
                result = alipayh5Service.notifyCheck(request, sn);
            } else if ("weixinScanPaymentPlugin".equals(pluginId)) {//微信扫描支付
                result = wechatScanService.notifyCheck(request, response, sn);
            } else if ("weixinH5PaymentPlugin".equals(pluginId)) {//h5支付
                result = wechatH5Service.notifyCheck(request, response, sn);
            } else if ("weiscan".equals(pluginId)) {
                result = wechatScanService.notifyCheck(request, response, sn);
            }
            out.print(result);
        } catch (Exception e) {
            out.println("fail");
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    @RequestMapping({"/notifyRefund/{pluginId}/{sn}.json"})
    public void notifyRefund(HttpServletRequest request,
        @PathVariable String pluginId,
        @PathVariable String sn,
        HttpServletResponse response) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            String result = "failure";
            if ("alipayMobilePaymentPlugin".equals(pluginId)) {//阿里手机支付
                result = alipayMobileService.notifyCheck(request, sn);
            } else if ("unionpayMobilePaymentPlugin".equals(pluginId)) {//银联手机支付
                result = unionpayService.notifyCheck(request, sn);
            } else if ("weixinMobilePaymentPlugin".equals(pluginId)) {//微信手机支付
                result = wechatMobileService.notifyCheck(request, response, sn);
            } else if ("weixinInternaPaymentPlugin".equals(pluginId)) {//微信国际支付
                //result = wechatMobileService.notifyCheck(request);
            } else if ("alipayInternaPaymentPlugin".equals(pluginId)) {//alip国际支付
                result = alipayInternaService.notifyCheck(request, sn);
            } else if ("quickBillPaymentPlugin".equals(pluginId)) {//块钱
                result = billService.notifyCheck(request, sn);
            } else if ("alipayH5PaymentPlugin".equals(pluginId)) {
                result = alipayh5Service.notifyCheck(request, sn);
            } else if ("weixinScanPaymentPlugin".equals(pluginId)) {//微信扫描支付
                result = wechatScanService.notifyCheck(request, response, sn);
            } else if ("weixinH5PaymentPlugin".equals(pluginId)) {//h5支付
                result = wechatH5Service.notifyCheck(request, response, sn);
            }
            out.print(result);
        } catch (Exception e) {
            out.println("fail");
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    /**
     * 支付前端页面
     */
    @RequestMapping({"/payFront/{pluginId}/{sn}.json"})
    public void payFront(HttpServletRequest request,
        @PathVariable String pluginId,
        @PathVariable String sn,
        HttpServletResponse response) {
        PrintWriter out = null;
        try {
            out = response.getWriter();
            String result = "failure";
            if ("alipayMobilePaymentPlugin".equals(pluginId)) {//阿里手机支付
                result = alipayMobileService.notifyCheck(request, sn);
            } else if ("unionpayMobilePaymentPlugin".equals(pluginId)) {//银联手机支付
                result = unionpayService.notifyCheck(request, sn);
            } else if ("weixinMobilePaymentPlugin".equals(pluginId)) {//微信手机支付
                result = wechatMobileService.notifyCheck(request, response, sn);
            } else if ("weixinInternaPaymentPlugin".equals(pluginId)) {//微信国际支付
                //result = wechatMobileService.notifyCheck(request);
            } else if ("alipayInternaPaymentPlugin".equals(pluginId)) {//alip国际支付
                result = alipayInternaService.notifyCheck(request, sn);
            } else if ("quickBillPaymentPlugin".equals(pluginId)) {//块钱
                result = billService.notifyCheck(request, sn);
            } else if ("alipayH5PaymentPlugin".equals(pluginId)) {
                result = alipayh5Service.notifyCheck(request, sn);
            } else if ("weixinScanPaymentPlugin".equals(pluginId)) {//微信扫描支付
                result = wechatScanService.notifyCheck(request, response, sn);
            } else if ("weixinH5PaymentPlugin".equals(pluginId)) {
                result = wechatH5Service.notifyCheck(request, response, sn);
            }
            out.print(result);
        } catch (Exception e) {
            out.println("fail");
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    @RequestMapping(value = {"/getJsApi"}, method = {RequestMethod.GET,
        RequestMethod.POST})
    public
    @ResponseBody
    String getJsApi(String url) {
        WachatContent.initPayConfig(tSystemPluginConfigService.readPlug("weixinH5PaymentPlugin"));
        String jsapi_ticket = wechatH5Service
            .jsTicket(WachatContent.appid, WachatContent.appsecret);//appid 公众账号唯一标示 ，appsecret 公众账号的秘钥
        WxJSApiResult result = new WxJSApiResult();
        try {
            long time = System.currentTimeMillis() / 1000;
            String randomStr = UUID.randomUUID().toString();
            String str = "jsapi_ticket=" + jsapi_ticket
                + "&noncestr=" + randomStr + "&timestamp=" + time + "&url="
                + url;
            String signature = WachatContent.sha1Encrypt(str);
            result.setAppId(WachatContent.appid);
            result.setNonceStr(randomStr);
            result.setSignature(signature);
            result.setTimestamp(Long.toString(time));
            Map<String, Object> params = new HashMap<>();
            params.put("code", 1);
            params.put("object", result);
            return JacksonUtil.toJson(params);
        } catch (Exception e) {
            Map<String, Object> params = new HashMap<>();
            params.put("code", 0);
            return JacksonUtil.toJson(params);
        }
    }
}
