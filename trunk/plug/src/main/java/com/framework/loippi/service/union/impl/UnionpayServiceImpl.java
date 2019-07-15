package com.framework.loippi.service.union.impl;

import com.framework.loippi.entity.PayCommon;
import com.framework.loippi.service.PaymentService;
import com.framework.loippi.service.TSystemPluginConfigService;
import com.framework.loippi.utils.unionpay.mobile.config.AppConsumeContents;
import com.framework.loippi.service.union.UnionpayService;
import com.framework.loippi.utils.unionpay.pc.BaseUtil;
import com.framework.loippi.utils.unionpay.pc.sdk.*;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

@Service
@Slf4j
public class UnionpayServiceImpl implements UnionpayService {

    @Resource
    private PaymentService paymentService;
    @Resource
    private TSystemPluginConfigService tSystemPluginConfigService;

    @PostConstruct
    public void init() {
        SDKConfig.getConfig().loadPropertiesFromSrc();
        CertUtil.init();
        AppConsumeContents.initConfig(tSystemPluginConfigService.readPlug("unionpayMobilePaymentPlugin"));
    }

    public String prePay(PayCommon payCommon, HttpServletRequest request) {
        AppConsumeContents.initConfig(tSystemPluginConfigService.readPlug("unionpayMobilePaymentPlugin"));
        Map<String, String> contentData = new HashMap<String, String>();
        contentData.put("version", "5.0.0");                                                    //版本号 全渠道默认值
        contentData.put("encoding", AppConsumeContents.encoding);                                        //字符集编码 可以使用UTF-8,GBK两种方式
        contentData.put("signMethod", "01");                                                    //签名方法 目前只支持01：RSA方式证书加密
        contentData.put("txnType", "01");                                                        //交易类型 01:消费
        contentData.put("txnSubType", "01");                                                    //交易子类 01：消费
        contentData.put("bizType", "000201");                                                    //填写000201
        contentData.put("channelType", "08");                                                    //渠道类型 08手机

        /***商户接入参数***/
        contentData.put("merId", AppConsumeContents.merId);                        //商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
        contentData.put("accessType", "0");                                                        //接入类型，商户接入填0 ，不需修改（0：直连商户， 1： 收单机构 2：平台商户）
        contentData.put("orderId", payCommon.getOutTradeNo());                                                            //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
        contentData.put("txnTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));    //订单发送时间，取系统时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
        contentData.put("accType", "01");                                                        //账号类型 01：银行卡02：存折03：IC卡帐号类型(卡介质)
        contentData.put("txnAmt", payCommon.getPayAmount().multiply(new BigDecimal(100)).intValue() + "");                    //交易金额 单位为分，不能带小数点
        contentData.put("currencyCode", "156");                                                //境内商户固定 156 人民币
        contentData.put("backUrl", payCommon.getNotifyUrl());

        Map<String, String> submitFromData = BaseUtil.signData(contentData, new AppConsumeContents().certPath, AppConsumeContents.certPwd);
        // 交易请求url 从配置文件读取
        String requestAppUrl = SDKConfig.getConfig().getAppRequestUrl();
        System.out.println("url:*******:" + requestAppUrl);
        System.out.println("submit:&&&&&&&&&:" + submitFromData);
        Map<String, String> resmap = BaseUtil.submitUrl(submitFromData, requestAppUrl);
        if (!resmap.isEmpty()) {
            if (resmap.containsKey("respCode")) {
                Object resCodeValue = resmap.get("respCode");
                if (resCodeValue != null && "00".equals(resCodeValue.toString())) {
                    if (resmap.containsKey("tn")) {
                        return resmap.get("tn");
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Map<String, Object> Unionpayfront(HttpServletRequest request, HttpServletResponse rep) {
        //获取本地商户私钥证书（签名），银联公钥证书（验签）等地址
        //String relapath=SpringContextUtil.getResourceRootRealPath()+File.separator+"com/leimingtech/extend/module/payment/unionpay/pc";
        //String relapath=Acpsdkurl.class.getResource("").toString();
        //加载配置acp_sdk.properties文件
        //SDKConfig.getConfig().loadPropertiesFromPath(relapath);//loadPropertiesFromSrc();
        LogUtil.writeLog("FrontRcvResponse前台接收报文返回开始");
//		PrintWriter out = null;
        Map<String, Object> payMap = new HashMap<String, Object>();
        payMap.put("paystate", "failure");
//		try {
//			out= rep.getWriter();
//		}catch (IOException e2){
//			e2.printStackTrace();
//		}
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        String encoding = request.getParameter(SDKConstants.param_encoding);
        log.debug("返回报文中encoding=[" + encoding + "]");
        /*String pageResult = "";
        if ("UTF-8".equalsIgnoreCase(encoding)) {
			pageResult = "/cart/pay_result";
		} else {
			pageResult = "/cart/pay_result";
		}*/
        Map<String, String> respParam = getAllRequestParam(request);
        // 打印请求报文
        LogUtil.printRequestLog(respParam);
        Map<String, String> valideData = null;
        StringBuffer page = new StringBuffer();
        if (null != respParam && !respParam.isEmpty()) {
            Iterator<Entry<String, String>> it = respParam.entrySet().iterator();
            valideData = new HashMap<String, String>(respParam.size());
            while (it.hasNext()) {
                Entry<String, String> e = it.next();
                String key = (String) e.getKey();
                String value = (String) e.getValue();
                try {
                    value = new String(value.getBytes("UTF-8"), encoding);
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                page.append("<tr><td width=\"30%\" align=\"right\">" + key
                        + "(" + key + ")</td><td>" + value + "</td></tr>");
                valideData.put(key, value);
            }
        }
        if (!SDKUtil.validate(valideData, encoding)) {
            page.append("<tr><td width=\"30%\" align=\"right\">验证签名结果</td><td>失败</td></tr>");
            log.debug("验证签名结果[失败].");
            //out.println("failure");
            payMap.put("paystate", "failure");
            payMap.put("msg", "验证签名结果[失败]");
        } else {
            System.out.println("ordersn:**********" + valideData.get("orderId"));
            page.append("<tr><td width=\"30%\" align=\"right\">验证签名结果</td><td>成功</td></tr>");
            log.debug("验证签名结果[成功].");
            //payService.updatePayFinish(valideData.get("orderId"));
            System.out.println(valideData.get("orderId")); //其他字段也可用类似方式获取
            //out.print("success");//向银行发送处理结果
            String respCode = valideData.get("respCode");//获取应答码，收到后台通知了respCode的值一般是00
            if (respCode.equals("00")) {
                payMap.put("paystate", "success");
                payMap.put("out_trade_no", valideData.get("orderId"));
                payMap.put("trade_no", valideData.get("queryId"));//交易流水号
                payMap.put("txnAmt", valideData.get("txnAmt"));//交易金额
                payMap.put("msg", "验证签名结果[成功]");
            }
        }
        request.setAttribute("result", page.toString());
        log.debug("FrontRcvResponse前台接收报文返回结束");
        return payMap;
    }

    @Override
    public Map<String, Object> Unionpayback(HttpServletRequest request, HttpServletResponse resp) {
        //获取本地商户私钥证书（签名），银联公钥证书（验签）等地址
        //String relapath=SpringContextUtil.getResourceRootRealPath()+File.separator+"com/leimingtech/extend/module/payment/unionpay/pc";
        //String relapath=Acpsdkurl.class.getResource("").toString();
        //加载配置acp_sdk.properties文件
        //SDKConfig.getConfig().loadPropertiesFromPath(relapath);//loadPropertiesFromSrc();
        //PrintWriter out = null;
        //String mes="";
        Map<String, Object> payMap = new HashMap<String, Object>();
        payMap.put("paystate", "failure");
//		try {
//			out= resp.getWriter();
//		} catch (IOException e2) {
//			e2.printStackTrace();
//		}
        log.debug("BackRcvResponse接收后台通知开始");
        try {
            request.setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        String encoding = request.getParameter(SDKConstants.param_encoding);
        // 获取请求参数中所有的信息
        Map<String, String> reqParam = getAllRequestParam(request);
        // 打印请求报文
        LogUtil.printRequestLog(reqParam);
        Map<String, String> valideData = null;
        if (null != reqParam && !reqParam.isEmpty()) {
            Iterator<Entry<String, String>> it = reqParam.entrySet().iterator();
            valideData = new HashMap<String, String>(reqParam.size());
            while (it.hasNext()) {
                Entry<String, String> e = it.next();
                String key = (String) e.getKey();
                String value = (String) e.getValue();
                try {
                    value = new String(value.getBytes("UTF-8"), encoding);
                } catch (UnsupportedEncodingException e1) {
                    e1.printStackTrace();
                }
                valideData.put(key, value);
            }
        }
        // 验证签名
        if (!SDKUtil.validate(valideData, encoding)) {
            log.debug("验证签名结果[失败].");
            payMap.put("paystate", "failure");
            payMap.put("msg", "验证签名结果[失败]");
//				out.println("failure");
//				mes="false";
        } else {
            //payService.updatePayFinish(valideData.get("orderId"));
            log.debug("验证签名结果[成功].");
            //String respMsg=valideData.get("respMsg");//应答信息
            String respCode = valideData.get("respCode");//获取应答码，收到后台通知了respCode的值一般是00
            if (respCode.equals("00")) {
                payMap.put("paystate", "success");
                payMap.put("out_trade_no", valideData.get("orderId"));
                payMap.put("trade_no", valideData.get("queryId"));//交易流水号
                payMap.put("txnAmt", valideData.get("txnAmt"));//交易金额
                payMap.put("msg", "验证签名结果[成功]");
            }
        }
        log.debug("BackRcvResponse接收后台通知结束");
        return payMap;
    }

    @Override
    public String notifyCheck(HttpServletRequest request, String sn) {
        try {
            request.setCharacterEncoding("UTF-8");
            String encoding = request.getParameter(SDKConstants.param_encoding);
            // 获取请求参数中所有的信息
            Map<String, String> reqParam = getAllRequestParam(request);
            // 打印请求报文
            Map<String, String> valideData = null;
            if (null != reqParam && !reqParam.isEmpty()) {
                Iterator<Entry<String, String>> it = reqParam.entrySet().iterator();
                valideData = new HashMap<String, String>(reqParam.size());
                while (it.hasNext()) {
                    Entry<String, String> e = it.next();
                    String key = e.getKey();
                    String value = e.getValue();
                    if (!StringUtil.isEmpty(value)) {
                        valideData.put(key, value);
                    }
                }
            }
            // 验证签名
            String respCode = valideData.get("respCode");//获取应答码，收到后台通知了respCode的值一般是00
            String out_trade_no = valideData.get("orderId");
            String trade_no = valideData.get("queryId");//交易流水号
            if (respCode.equals("00")) {
                //银联返回金额单位为分
                String txnAmt = valideData.get("txnAmt");
                Float txnAmt2 = Float.valueOf(txnAmt) / 100;
                paymentService.updatePayBack(out_trade_no, trade_no, "mobile_unionpay", txnAmt2.toString());
                return "success"; // 验证失败成功
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "failure";
    }

    /**
     * 获取请求参数中所有的信息
     *
     * @param request
     * @return
     */
    public static Map<String, String> getAllRequestParam(
            final HttpServletRequest request) {
        Map<String, String> res = new HashMap<String, String>();
        Enumeration<?> temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String) temp.nextElement();
                String value = request.getParameter(en);
                res.put(en, value);
                // 在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
                if (res.get(en) == null || "".equals(res.get(en))) {
                    // System.out.println("======为空的字段名===="+en);
                    res.remove(en);
                }
            }
        }
        return res;
    }

}
