package com.framework.loippi.utils.quickbill.PC.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/* *
 *类名：BillSubmit
 *功能：快钱各接口请求提交类
 *详细：构造快钱各接口表单HTML文本，获取远程HTTP数据
 */

public class BillSubmit {
    
    /**
     * 快钱提供给商户的服务接入网关URL(新)
     */
    private static final String BILL_GATEWAY_NEW =" https://www.99bill.com/gateway/recvMerchantInfoAction.htm";



    /**
     * 建立请求，以表单HTML形式构造（默认）
     * @param sParaTemp 请求参数数组
     * @param strMethod 提交方式。两个值可选：post、get
     * @param strButtonName 确认按钮显示文字
     * @return 提交表单HTML文本
     */
    public static String buildRequest(Map<String, Object> sParaTemp, String strMethod, String strButtonName) {
        //待请求参数数组
        Map<String, Object> sPara = sParaTemp;
        List<String> keys = new ArrayList<String>(sPara.keySet());

        StringBuffer sbHtml = new StringBuffer();
        sbHtml.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"><html>"
        		+ "<head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">"
        		+ "<title>使用快钱支付</title></head>"
        		+ "<body>");
        sbHtml.append("<form id=\"kqPay\"  name=\"kqPay\" action=\"" + BILL_GATEWAY_NEW
                       + "\" method=\"" + strMethod
                      + "\">");

        for (int i = 0; i < keys.size(); i++) {
            String name = (String) keys.get(i);
            String value = (String) sPara.get(name);

            sbHtml.append("<input type=\"hidden\" name=\"" + name + "\" value=\"" + value + "\"/>");
        }
        //submit按钮控件请不要含有name属性
        sbHtml.append("<input type=\"submit\" value=\"" + strButtonName + "\" style=\"display:none;\"></form>");
        //页面加载完后自动提交订单
        sbHtml.append("<script>document.forms['kqPay'].submit();</script>");

        sbHtml.append("</body></html>");
        return sbHtml.toString();
    }
    

}
