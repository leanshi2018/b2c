package com.framework.loippi.utils.alipay.pc.china.util;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeRefundRequest;
import com.alipay.api.response.AlipayTradeRefundResponse;

import com.framework.loippi.utils.NumberUtils;
import com.framework.loippi.utils.validator.DateUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

/* *
 *类名：AlipaySubmit
 *功能：支付宝各接口请求提交类
 *详细：构造支付宝各接口表单HTML文本，获取远程HTTP数据
 *版本：3.3
 *日期：2012-08-13
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipaySubmit {

    /**
     * 支付宝提供给商户的服务接入网关URL(新)
     */
    private static final String ALIPAY_GATEWAY_NEW = "https://mapi.alipay.com/gateway.do?";

    /**
     * 建立请求，以表单HTML形式构造（默认）
     *
     * @return 提交表单HTML文本
     */
    public static String buildRequest(String outTradeNo, BigDecimal amount, String reason, String appId, String privateRsa, String publicRsa) {
        AlipayClient alipayClient = new DefaultAlipayClient(ALIPAY_GATEWAY_NEW, appId, privateRsa, "json", "utf-8", publicRsa, "RSA2");
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        request.setBizContent("{" +
                "\"out_trade_no\":\"" + outTradeNo + "\"," +
            "\"out_request_no\":\"" + DateUtils.getDateStr(new Date(), "yyyyMMddHHmmssSSS") + NumberUtils.getRandomNumber() + "\"," +
                "\"refund_amount\":" + amount.setScale(2, RoundingMode.HALF_UP).toString() + "}");
        //"\"refund_reason\":\"" + reason + "\"" + "}");
        AlipayTradeRefundResponse response = null;
        try {
            response = alipayClient.execute(request);
        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if (response.isSuccess()) {
            return "true";
        } else {
            return "false";
        }
    }

    /**
     * 建立错误的跳转请求，以表单HTML形式构造（默认）
     *
     * @param orderSn  订单号
     * @param errorMsg 错误信息
     * @return 提交表单HTML文本
     */
    public static String errorRedirect(String orderSn, String errorMsg, String payResult) {
        StringBuffer sbHtml = new StringBuffer();
        sbHtml.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"><html>"
                + "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
                + "<title>支付失败/title></head><body>");

        //页面加载完后自动跳转
        sbHtml.append("<script>location.href='" + payResult + "'</script>");

        sbHtml.append("</body></html>");
        return sbHtml.toString();
    }

}
