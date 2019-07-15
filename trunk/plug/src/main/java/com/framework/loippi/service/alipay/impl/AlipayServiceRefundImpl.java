package com.framework.loippi.service.alipay.impl;

import com.framework.loippi.entity.AliPayRefund;
import com.framework.loippi.service.alipay.AlipayRefundService;
import com.framework.loippi.service.TSystemPluginConfigService;
import com.framework.loippi.utils.alipay.mobile.config.AlipayConfig;
import com.framework.loippi.utils.alipay.pc.china.util.AlipayNotify;
import com.framework.loippi.utils.alipay.pc.china.util.AlipaySubmit;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AlipayServiceRefundImpl implements AlipayRefundService {

    @Resource
    private TSystemPluginConfigService tSystemPluginConfigService;

    @Override
    public String toRefund(AliPayRefund aliPayRefund) {
        AlipayConfig.initAlipayConfig(tSystemPluginConfigService.readPlug("alipayMobilePaymentPlugin"));
        //建立请求
        String sHtmlText = AlipaySubmit.buildRequest(aliPayRefund.getTradeNo(), aliPayRefund.getRefundAmount(),
                aliPayRefund.getRRefundReason(), AlipayConfig.APPID, AlipayConfig.private_key, AlipayConfig.ali_public_key);
        return sHtmlText;
    }

    public static void main(String[] args) {
        System.out.println("");
        Class <AlipaySubmit> s = AlipaySubmit.class;
    }

    @Override
    public Map<String, Object> Refundback(HttpServletRequest request) {
        //获取支付宝POST过来反馈信息
        Map<String, Object> payMap = new HashMap<String, Object>();
        payMap.put("refundstate", "fail");
        try {
            Map<String, String> params = new HashMap<String, String>();
            Map requestParams = request.getParameterMap();
            for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
                String name = (String) iter.next();
                String[] values = (String[]) requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i]
                            : valueStr + values[i] + ",";
                }
                //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
                //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
                params.put(name, valueStr);
            }

            //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
            //批次号
            String batch_no = new String(request.getParameter("batch_no").getBytes("ISO-8859-1"), "UTF-8");
            //批量退款数据中转账成功的笔数
            String success_num = new String(request.getParameter("success_num").getBytes("ISO-8859-1"), "UTF-8");
            //批量退款数据中的详细信息
            String result_details = new String(request.getParameter("result_details").getBytes("ISO-8859-1"), "UTF-8");
            //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
            if (AlipayNotify.verify(params)) {//验证成功
                //////////////////////////////////////////////////////////////////////////////////////////
                //请在这里加上商户的业务逻辑程序代码
                //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
                if (!"0".equals(success_num)) {
                    payMap.put("refundstate", "success");
                    payMap.put("batch_no", batch_no);
                    payMap.put("result_details", result_details);
                }
                //判断是否在商户网站中已经做过了这次通知返回的处理
                //如果没有做过处理，那么执行商户的业务程序
                //如果有做过处理，那么不执行商户的业务程序
                //请不要修改或删除
                //——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
                //////////////////////////////////////////////////////////////////////////////////////////
            } else {//验证失败
                payMap.put("refundstate", "fail");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            log.error("出错啦，支付失败", e);
            payMap.put("refundstate", "fail");
            payMap.put("msg", "出错啦，退款失败");
        }
        return payMap;
    }

//    @Override
//    public boolean transferMoeny(AlipayFundTransToaccountTransferModel model) {
//        AlipayConfig.initAlipayConfig(tSystemPluginConfigService.readPlug("alipayMobilePaymentPlugin"));
//        //建立请求
//        boolean isSuccess = false;
//        try {
//            isSuccess = AlipayUtilsTest.transfer(model, AlipayConfig.APPID, AlipayConfig.PRIVATE_KEY, AlipayConfig.ALI_PUBLIC_KEY);
//        } catch (AlipayApiException e) {
//            e.printStackTrace();
//        }
//        return isSuccess;
//    }

}
