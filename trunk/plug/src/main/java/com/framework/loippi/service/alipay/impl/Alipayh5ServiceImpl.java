package com.framework.loippi.service.alipay.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.framework.loippi.entity.PayCommon;
import com.framework.loippi.service.PaymentService;
import com.framework.loippi.utils.alipay.h5.config.AlipayConfig;
import com.framework.loippi.utils.alipay.h5.util.AlipayNotify;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.framework.loippi.utils.alipay.h5.util.AlipaySubmit;
import com.framework.loippi.service.alipay.Alipayh5Service;

@Service
@Slf4j
public class Alipayh5ServiceImpl implements Alipayh5Service {
    @Resource
    private PaymentService payService;

    public String prePay(PayCommon payCommon) {
        // //////////////////////////////////请求参数//////////////////////////////////////
        // 支付类型
        //Pay pay = payService.findPayBySn(orderSn);
        //String bodyStr = pay.getBeWrite();
        String payment_type = "1";
        // 必填，不能修改
        // 服务器异步通知页面路径
        String notify_url = payCommon.getNotifyUrl();
        // 需http://格式的完整路径，不能加?id=123这类自定义参数
        // 页面跳转同步通知页面路径
        String return_url = payCommon.getReturnUrl();
        // 需http://格式的完整路径，不能加?id=123这类自定义参数，不能写成http://localhost/
        // 商户订单号
        //String out_trade_no = pay.getPaySn();
        String out_trade_no = payCommon.getOutTradeNo();
        // 商户网站订单系统中唯一订单号，必填
        // 必填
        // 付款金额
        String total_fee = payCommon.getPayAmount() + "";
        // 必填
        // 商品展示地址
        //String show_url = ComContents.payserviceurl+"/m/goods/goodsdetail?id=34";
        // 必填，需以http://开头的完整路径，例如：http://www.商户网址.com/myorder.html
        // 订单描述
        // 选填
        // 超时时间
        String it_b_pay = "";
        // 选填
        // 钱包token
        String extern_token = "";
        // 选填
        // ////////////////////////////////////////////////////////////////////////////////
        // 把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", "alipay.wap.create.direct.pay.by.user");
        sParaTemp.put("partner", AlipayConfig.partner);
        sParaTemp.put("seller_id", AlipayConfig.seller_id);
        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
        sParaTemp.put("payment_type", payment_type);
        sParaTemp.put("notify_url", notify_url);
        sParaTemp.put("return_url", return_url);
        sParaTemp.put("out_trade_no", out_trade_no);
        sParaTemp.put("subject", payCommon.getTitle());
        sParaTemp.put("total_fee", total_fee);
        sParaTemp.put("show_url", payCommon.getShowUrl());
        sParaTemp.put("body", payCommon.getBody());
        sParaTemp.put("it_b_pay", it_b_pay);
        sParaTemp.put("extern_token", extern_token);
        // 建立请求
        String sHtmlText = AlipaySubmit.buildRequest(sParaTemp, "get", "确认");
        return sHtmlText;
    }

    @Override
    public String notifyCheck(HttpServletRequest request, String sn) {
        // 获取支付宝POST过来反馈信息
        try {
            Map<String, String> params = new HashMap<String, String>();
            Map requestParams = request.getParameterMap();
            for (Iterator iter = requestParams.keySet().iterator(); iter
                    .hasNext(); ) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                // 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
                // valueStr = new String(valueStr.getBytes("ISO-8859-1"),
                // "gbk");
                params.put(name, valueStr);
            }
            // 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
            // 商户订单号
            String out_trade_no = new String(request.getParameter(
                    "out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            // 支付宝交易号
            String trade_no = new String(request.getParameter("trade_no")
                    .getBytes("ISO-8859-1"), "UTF-8");
            // 交易状态
            String trade_status = new String(request.getParameter(
                    "trade_status").getBytes("ISO-8859-1"), "UTF-8");
            //交易金额
            String total_fee = new String(request.getParameter("total_fee").getBytes("ISO-8859-1"), "UTF-8");
            // 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
            if (AlipayNotify.verify(params)) {// 验证成功
                // ////////////////////////////////////////////////////////////////////////////////////////
                // 请在这里加上商户的业务逻辑程序代码
                // ——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
                if (trade_status.equals("TRADE_FINISHED")) {
                    // 判断该笔订单是否在商户网站中已经做过处理
                    // 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    // 如果有做过处理，不执行商户的业务程序
                    // 注意：
                    // 退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
                } else if (trade_status.equals("TRADE_SUCCESS")) {
                    // 判断该笔订单是否在商户网站中已经做过处理
                    // 如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
                    // 如果有做过处理，不执行商户的业务程序
                    // 付款完成后，支付宝系统发送该交易状态通知
                    payService.updatePayBack(out_trade_no, trade_no, "alipay", total_fee);
                    return "success";
                }
                // ——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
                // ////////////////////////////////////////////////////////////////////////////////////////
                return "fail";
            } else {// 验证失败
//				log.error(e.toString());
                return "fail";
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("", e);
            return "fail";
        }
    }

}
