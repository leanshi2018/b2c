package com.framework.loippi.service.alipay.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.framework.loippi.entity.AliPayBathBack;
import com.framework.loippi.utils.alipay.pc.china.config.AlipayConfig;
import com.framework.loippi.utils.validator.DateUtils;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import com.framework.loippi.utils.alipay.pc.china.util.AlipayNotify;
import com.framework.loippi.service.alipay.BatchBackAlipayService;

@Service
@Slf4j
public class BatchBackAlipayServiceImpl implements BatchBackAlipayService {
    @Override
    public String toBatchBack(AliPayBathBack aliPayBathBack) {
        //服务器异步通知页面路径
        String notify_url = aliPayBathBack.getNotifyurl();
        //需http://格式的完整路径，不允许加?id=123这类自定义参数
        //付款账号
        String email = AlipayConfig.partner;
        //必填
        //付款账户名
        String account_name = AlipayConfig.seller_email;
        //必填，个人支付宝账号是真实姓名公司支付宝账号是公司名称
        //付款当天日期
        //String pay_date = DateUtils.getnowDate();
        String pay_date = DateUtils.getnowDate();
        //必填，格式：年[4位]月[2位]日[2位]，如：20100801
        //批次号
        String batch_no = aliPayBathBack.getBatchCountNum();
        //必填，格式：当天日期[8位]+序列号[3至16位]，如：201008010000001
        //付款总金额
        String batch_fee = aliPayBathBack.getBatchFee();
        //必填，即参数detail_data的值中所有金额的总和
        //付款笔数
        String batch_num = aliPayBathBack.getBatchNum();
        //必填，即参数detail_data的值中，“|”字符出现的数量加1，最大支持1000笔（即“|”字符出现的数量999个）
        //必填，格式：流水号1^收款方帐号1^真实姓名^付款金额1^备注说明1|流水号2^收款方帐号2^真实姓名^付款金额2^备注说明2....
        //付款详细数据
        String detail_data = aliPayBathBack.getDetailData();
        try {
            account_name = new String(account_name.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //把请求参数打包成数组
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", "batch_trans_notify");
        sParaTemp.put("partner", AlipayConfig.partner);
        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
        sParaTemp.put("notify_url", notify_url);
        sParaTemp.put("email", email);
        sParaTemp.put("account_name", account_name);
        sParaTemp.put("pay_date", pay_date);
        sParaTemp.put("batch_no", batch_no);
        sParaTemp.put("batch_fee", batch_fee);
        sParaTemp.put("batch_num", batch_num);
        sParaTemp.put("detail_data", detail_data);
        //建立请求
        //String sHtmlText = AlipaySubmit.buildRequest(sParaTemp, "get", "确认");
        return "";
    }

    @Override
    public Map<String, Object> BatchBack(HttpServletRequest request) {
        //获取支付宝POST过来反馈信息
        Map<String, Object> payMap = new HashMap<String, Object>();
        payMap.put("batchstate", "fail");
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
            //批量付款数据中转账成功的详细信息
            String success_details = "";
            if (request.getParameter("success_details") != null && StringUtils.isNotEmpty(request.getParameter("success_details"))) {
                success_details = new String(request.getParameter("success_details").getBytes("ISO-8859-1"), "UTF-8");
            }
            //批量付款数据中转账失败的详细信息
            String fail_details = "";
            if (request.getParameter("fail_details") != null && StringUtils.isNotEmpty(request.getParameter("fail_details"))) {
                fail_details = new String(request.getParameter("fail_details").getBytes("ISO-8859-1"), "UTF-8");
            }
            //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
            if (AlipayNotify.verify(params)) {//验证成功
                //请在这里加上商户的业务逻辑程序代码
                //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
                payMap.put("batchstate", "success");
                payMap.put("success_details", success_details);
                payMap.put("fail_details", fail_details);
                //判断是否在商户网站中已经做过了这次通知返回的处理
                //如果没有做过处理，那么执行商户的业务程序
                //如果有做过处理，那么不执行商户的业务程序
                //请不要修改或删除
                //——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
                //////////////////////////////////////////////////////////////////////////////////////////
            } else {//验证失败
                payMap.put("batchstate", "fail");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            log.error("出错啦，支付失败", e);
            payMap.put("batchstate", "fail");
            payMap.put("msg", "出错啦，退款失败");
        }
        return payMap;
    }

}
