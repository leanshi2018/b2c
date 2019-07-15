package com.framework.loippi.service.wechat.impl;

import com.framework.loippi.utils.wechat.mobile.util.WeixinUtils;

import java.math.BigDecimal;
import java.net.InetAddress;
import java.util.HashMap;

import com.framework.loippi.entity.PayCommon;
import com.framework.loippi.service.PaymentService;
import com.framework.loippi.utils.wechat.h5.config.WachatContent;
import com.framework.loippi.utils.wechat.h5.handler.GetWxOrderno;
import com.framework.loippi.utils.wechat.h5.handler.RequestHandler;
import com.framework.loippi.utils.wechat.h5.handler.TenpayUtil;
import com.framework.loippi.utils.wechat.mobile.util.component.ResponseHandler;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.framework.loippi.service.wechat.WechatScanService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Service
public class WechatScanServiceImpl implements WechatScanService {

    @Resource
    private PaymentService paymentService;

    @Override
    public String prePay(PayCommon payCommon) {
        // 1 参数
        // 总金额以分为单位，不带小数点
        String totalFee = payCommon.getPayAmount().multiply(new BigDecimal(100)).intValue() + "";
        // 订单生成的机器 IP
        InetAddress ia = null;
//        String spbill_create_ip =ia.getHostAddress();//获取本地ip
        String spbill_create_ip = "123.23.11.11";//获取本地ip
        // 这里notify_url是 支付完成后微信发给该链接信息，可以判断会员是否支付成功，改变订单状态等。
        String notify_url = payCommon.getNotifyUrl();
        String trade_type = "NATIVE";
        // 商户号
        String mch_id = WachatContent.partner;
        //appid
        String appid = WachatContent.appid;
        //应用密钥
        String appsecret = WachatContent.appsecret;
        //API密钥，在商户平台设置
        String apikey = WachatContent.apikey;
        // 随机字符串
        String nonce_str = WeixinUtils.createNoncestr();
        // 商品描述根据情况修改
        String body = payCommon.getBody();
        // 商户订单号
        String out_trade_no = payCommon.getOutTradeNo();

        HashMap<String, String> packageParams = new HashMap<>();
        packageParams.put("appid", WachatContent.appid);
        packageParams.put("mch_id", mch_id);
        packageParams.put("nonce_str", nonce_str);
        packageParams.put("body", body);
//        packageParams.put("attach", payCommon.getTitle());
        packageParams.put("out_trade_no", out_trade_no);
        // 这里写的金额为1 分到时修改
        packageParams.put("total_fee", totalFee);
        packageParams.put("spbill_create_ip", spbill_create_ip);
        packageParams.put("notify_url", notify_url);
        packageParams.put("trade_type", trade_type);

        RequestHandler reqHandler = new RequestHandler();
        reqHandler.init(appid, appsecret, apikey);
        String sign = WeixinUtils.sign(
            WeixinUtils.FormatBizQueryParaMap(packageParams, false), WachatContent.apikey);
        String xml = "<xml>" + "<appid>" + appid + "</appid>" + "<mch_id>"
            + mch_id + "</mch_id>" + "<nonce_str>" + nonce_str
            + "</nonce_str>" + "<sign>" + sign + "</sign>"
            + "<body><![CDATA[" + body + "]]></body>"
            + "<out_trade_no>" + out_trade_no
            + "</out_trade_no>"
//            + "<attach>" + payCommon.getTitle() + "</attach>"
            + "<total_fee>" + totalFee + "</total_fee>"
            + "<spbill_create_ip>" + spbill_create_ip
            + "</spbill_create_ip>" + "<notify_url>" + notify_url
            + "</notify_url>" + "<trade_type>" + trade_type
            + "</trade_type>" + "</xml>";
        String code_url = "";
        String createOrderURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        code_url = new GetWxOrderno().getCodeUrl(createOrderURL, xml);
        System.out.println("code_url----------------" + code_url);
        return code_url;
    }

    public String notifyCheck(HttpServletRequest request, HttpServletResponse response, String data) {
        // 创建支付应答对象
        ResponseHandler resHandler = new ResponseHandler(request, response);
        resHandler.setKey(WachatContent.apikey);
        resHandler.getAllParameters();
        try {
            //签名验证
            if (resHandler.isWechatSign()) {
                //根据反过来支付信息修改订单状态
                String result_code = resHandler.getSmap().get("result_code");// 业务结果
                if (result_code != null && result_code.equals("SUCCESS")) {
                    String total_fee = resHandler.getSmap().get("total_fee");
                    String sn = resHandler.getSmap().get("out_trade_no");
                    System.out.println("修改成功了" + result_code);//原商户订单
                    System.out.println("修改成功了" + resHandler.getSmap().get("transaction_id"));//微信支付订单号
                    //根据订单号修改订单信息
                    Float total_fee2 = Float.valueOf(total_fee) / 100;
                    paymentService.updatePayBack(sn, resHandler.getSmap().get("transaction_id"), "scan_weichatpay",
                        total_fee2.toString());//公共平台支付
                    return "SUCCESS";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "fail";
    }

    /**
     * 获取随机字符串
     */
    private String getNonceStr() {
        // 随机数
        String currTime = TenpayUtil.getCurrTime();
        // 8位日期
        String strTime = currTime.substring(8, currTime.length());
        // 四位随机数
        String strRandom = TenpayUtil.buildRandom(4) + "";
        // 10位序列号,可以自行调整。
        return strTime + strRandom;
    }

}
