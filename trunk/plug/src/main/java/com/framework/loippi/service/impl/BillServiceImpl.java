package com.framework.loippi.service.impl;

import com.framework.loippi.entity.PayCommon;
import com.framework.loippi.service.BillService;
import com.framework.loippi.service.PaymentService;
import com.framework.loippi.service.TUserSettingService;
import com.framework.loippi.utils.quickbill.PC.config.BillContents;
import com.framework.loippi.utils.quickbill.PC.util.BillSubmit;
import com.framework.loippi.utils.quickbill.PC.util.BillUtil;
import com.framework.loippi.utils.quickbill.PC.util.MD5Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class BillServiceImpl implements BillService {

    @Resource
    private PaymentService paymentService;
    @Resource
    private TUserSettingService tUserSettingService;

    public String prePay(PayCommon payCommon) {
        //人民币网关账户号
        ///请登录快钱系统获取用户编号，用户编号后加01即为人民币网关账户号。
        String merchantAcctId = BillContents.merchantAcctId;

        //人民币网关密钥
        ///区分大小写.请与快钱联系索取
        String key = BillContents.key;

        //字符集.固定选择值。可为空。
        ///只能选择1、2、3.
        ///1代表UTF-8; 2代表GBK; 3代表gb2312
        ///默认值为1
        String inputCharset = BillContents.inputCharset;

        //服务器接受支付结果的后台地址.与[pageUrl]不能同时为空。必须是绝对地址。
        ///快钱通过服务器连接的方式将交易结果发送到[bgUrl]对应的页面地址，在商户处理完成后输出的<result>如果为1，页面会转向到<redirecturl>对应的地址。
        ///如果快钱未接收到<redirecturl>对应的地址，快钱将把支付结果GET到[pageUrl]对应的页面。
        String bgUrl = payCommon.getNotifyUrl();

        //网关版本.固定值
        ///快钱会根据版本号来调用对应的接口处理程序。
        ///本代码版本号固定为v2.0
        //String version = BillContents.version;
        String version = BillContents.version;

        //语言种类.固定选择值。
        ///只能选择1、2、3
        ///1代表中文；2代表英文
        ///默认值为1
        String language = BillContents.language;

        //签名类型.固定值
        ///1代表MD5签名 2代表pki加密 4代表RSA或者DSA
        ///当前版本固定为1
        String signType = BillContents.signType;

        //支付人姓名
        ///可为中文或英文字符
        String payerName = "payerName";

        //支付人联系方式类型.固定选择值
        ///只能选择1
        ///1代表Email
        String payerContactType = "1";

        //支付人联系方式
        ///只能选择Email或手机号
        String payerContact = "";

        //商户订单号
        ///由字母、数字、或[-][_]组成
        String orderId = payCommon.getOutTradeNo();

        //订单金额
        ///以分为单位，必须是整型数字
        ///比方2，代表0.02元
        String orderAmount = payCommon.getPayAmount() + "";

        //订单提交时间
        ///14位数字。年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
        ///如；20080101010101
        String orderTime = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());

        //商品名称
        ///可为中文或英文字符
        String productName = "productName";

        //商品数量
        ///可为空，非空时必须为数字
        String productNum = "1";

        //商品代码
        ///可为字符或者数字
        String productId = "19";

        //商品描述
        String productDesc = "雷铭购物";

        //扩展字段1
        ///在支付结束后原样返回给商户
        String ext1 = payCommon.getExt1();
        //扩展字段2
        ///在支付结束后原样返回给商户
        String ext2 = "";

        //支付方式.固定选择值
        ///只能选择00、10、11、12、13、14
        ///00：组合支付（网关支付页面显示快钱支持的各种支付方式，推荐使用）10：银行卡支付（网关支付页面只显示银行卡支付）.11：电话银行支付（网关支付页面只显示电话支付）.12：快钱账户支付（网关支付页面只显示快钱账户支付）.13：线下支付（网关支付页面只显示线下支付方式）
        String payType = "00";


        //同一订单禁止重复提交标志
        ///固定选择值： 1、0
        ///1代表同一订单号只允许提交1次；0表示同一订单号在没有支付成功的前提下可重复提交多次。默认为0建议实物购物车结算类商户采用0；虚拟产品类商户采用1
        String redoFlag = "0";

        //WW如未和快钱签订代理合作协议，不需要填写本参数
        String pid = "";

        //生成加密签名串
        ///请务必按照如下顺序和规则组成加密串！
        String signMsgVal = "";
        signMsgVal = appendParam(signMsgVal, "inputCharset", inputCharset);
        signMsgVal = appendParam(signMsgVal, "bgUrl", bgUrl);
        signMsgVal = appendParam(signMsgVal, "version", version);
        signMsgVal = appendParam(signMsgVal, "language", language);
        signMsgVal = appendParam(signMsgVal, "signType", signType);
        signMsgVal = appendParam(signMsgVal, "merchantAcctId", merchantAcctId);
        signMsgVal = appendParam(signMsgVal, "payerName", payerName);
        signMsgVal = appendParam(signMsgVal, "payerContactType", payerContactType);
        signMsgVal = appendParam(signMsgVal, "payerContact", payerContact);
        signMsgVal = appendParam(signMsgVal, "orderId", orderId);
        signMsgVal = appendParam(signMsgVal, "orderAmount", orderAmount);
        signMsgVal = appendParam(signMsgVal, "orderTime", orderTime);
        signMsgVal = appendParam(signMsgVal, "productName", productName);
        signMsgVal = appendParam(signMsgVal, "productNum", productNum);
        signMsgVal = appendParam(signMsgVal, "productId", productId);
        signMsgVal = appendParam(signMsgVal, "productDesc", productDesc);
        signMsgVal = appendParam(signMsgVal, "ext1", ext1);
        signMsgVal = appendParam(signMsgVal, "ext2", ext2);
        signMsgVal = appendParam(signMsgVal, "payType", payType);
        signMsgVal = appendParam(signMsgVal, "redoFlag", redoFlag);
        signMsgVal = appendParam(signMsgVal, "pid", pid);
        signMsgVal = appendParam(signMsgVal, "key", key);
        String signMsg = "";
        //获取签名
        try {
            signMsg = MD5Util.md5Hex(signMsgVal.getBytes("UTF-8")).toUpperCase();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Map<String, Object> billmap = BillUtil.transStringToMap(signMsgVal, signMsg);
        //建立请求
        String sHtmlText = BillSubmit.buildRequest(billmap, "post", "提交到快钱");
        return sHtmlText;
    }

    private Map<String, Object> payback(HttpServletRequest request) {
        String out_trade_no = "";
        Map<String, Object> payMap = new HashMap<String, Object>();
        payMap.put("paystate", "!fail");
        payMap.put("rtnUrl", "");
        //获取人民币网关账户号
        String merchantAcctId = BillContents.merchantAcctId;
        //设置人民币网关密钥
        ///区分大小写
        String key = BillContents.key;

        //获取网关版本.固定值
        ///快钱会根据版本号来调用对应的接口处理程序。
        ///本代码版本号固定为v2.0
        String version = BillContents.version;

        //获取语言种类.固定选择值。
        ///只能选择1、2、3
        ///1代表中文；2代表英文
        ///默认值为1
        String language = BillContents.language;

        //签名类型.固定值
        ///1代表MD5签名
        ///当前版本固定为1
        String signType = BillContents.signType;

        //获取支付方式
        ///值为：10、11、12、13、14
        ///00：组合支付（网关支付页面显示快钱支持的各种支付方式，推荐使用）10：银行卡支付（网关支付页面只显示银行卡支付）.11：电话银行支付（网关支付页面只显示电话支付）.12：快钱账户支付（网关支付页面只显示快钱账户支付）.13：线下支付（网关支付页面只显示线下支付方式）.14：B2B支付（网关支付页面只显示B2B支付，但需要向快钱申请开通才能使用）
        String payType = request.getParameter("payType").trim();

        //获取银行代码
        ///参见银行代码列表
        String bankId = request.getParameter("bankId").trim();

        //获取商户订单号
        String orderId = request.getParameter("orderId").trim();

        //获取订单提交时间
        ///获取商户提交订单时的时间.14位数字。年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
        ///如：20080101010101
        String orderTime = request.getParameter("orderTime").trim();

        //获取原始订单金额
        ///订单提交到快钱时的金额，单位为分。
        ///比方2 ，代表0.02元
        String orderAmount = request.getParameter("orderAmount").trim();

        //获取快钱交易号
        ///获取该交易在快钱的交易号
        String dealId = request.getParameter("dealId").trim();

        //获取银行交易号
        ///如果使用银行卡支付时，在银行的交易号。如不是通过银行支付，则为空
        String bankDealId = request.getParameter("bankDealId").trim();

        //获取在快钱交易时间
        ///14位数字。年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
        ///如；20080101010101
        String dealTime = request.getParameter("dealTime").trim();

        //获取实际支付金额
        ///单位为分
        ///比方 2 ，代表0.02元
        String payAmount = request.getParameter("payAmount").trim();

        //获取交易手续费
        ///单位为分
        ///比方 2 ，代表0.02元
        String fee = request.getParameter("fee").trim();

        //获取扩展字段1
        String ext1 = request.getParameter("ext1").trim();

        //获取扩展字段2
        String ext2 = request.getParameter("ext2").trim();

        //获取处理结果
        ///10代表 成功11代表 失败
        String payResult = request.getParameter("payResult").trim();

        //获取错误代码
        ///详细见文档错误代码列表
        String errCode = request.getParameter("errCode").trim();

        //获取加密签名串
        String signMsg = request.getParameter("signMsg").trim();

        //生成加密串。必须保持如下顺序。
        String merchantSignMsgVal = "";
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "merchantAcctId", merchantAcctId);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "version", version);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "language", language);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "signType", signType);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "payType", payType);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "bankId", bankId);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderId", orderId);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderTime", orderTime);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "orderAmount", orderAmount);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "dealId", dealId);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "bankDealId", bankDealId);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "dealTime", dealTime);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "payAmount", payAmount);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "fee", fee);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext1", ext1);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "ext2", ext2);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "payResult", payResult);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "errCode", errCode);
        merchantSignMsgVal = appendParam(merchantSignMsgVal, "key", key);
        String merchantSignMsg = "";
        try {
            merchantSignMsg = MD5Util.md5Hex(merchantSignMsgVal.getBytes("UTF-8")).toUpperCase();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //初始化结果及地址
        int rtnOk = 0;
        String rtnUrl = "";
        //商家进行数据处理，并跳转会商家显示支付结果的页面
        //首先进行签名字符串验证
        if (signMsg.toUpperCase().equals(merchantSignMsg.toUpperCase())) {
            ///接着进行支付结果判断
            switch (Integer.parseInt(payResult)) {
                case 10:
                    //*
                    // 商户网站逻辑处理，比方更新订单支付状态为成功
                    // 特别注意：只有signMsg.toUpperCase().equals(merchantSignMsg.toUpperCase())，
                    // 且payResult=10，才表示支付成功！同时将订单金额与提交订单前的订单金额进行对比校验。
                    //*
                    //报告给快钱处理结果，并提供将要重定向的地址。
                    //rtnOk=1;
                    //rtnUrl="http://www.yoursite.com/show.jsp?msg=success!";
                    payMap.put("paystate", "success!");
                    payMap.put("out_trade_no", orderId);
                    //快钱交易号
                    payMap.put("trade_no", dealId);
                    payMap.put("rtnOk", 1);
                    break;
                default:
                    //rtnOk=1;
                    //rtnUrl="http://www.yoursite.com/show.jsp?msg=false!";
                    payMap.put("paystate", "false!");
                    payMap.put("rtnOk", 1);
                    break;
            }
        } else {
            //rtnOk=1;
            //rtnUrl="http://www.yoursite.com/show.jsp?msg=error!";
            payMap.put("paystate", "false!");
            payMap.put("rtnOk", 1);
        }
        return payMap;
    }


    //功能函数。将变量值不为空的参数组成字符串
    public String appendParam(String returnStr, String paramId, String paramValue) {
        if (!returnStr.equals("")) {
            if (!paramValue.equals("")) {
                returnStr = returnStr + "&" + paramId + "=" + paramValue;
            }
        } else {
            if (!paramValue.equals("")) {
                returnStr = paramId + "=" + paramValue;
            }
        }
        return returnStr;
    }

    @Override
    public String notifyCheck(HttpServletRequest request, String sn) {
        Map<String, Object> payMap = payback(request);
        if (payMap.size() != 0) {//out_trade_no返回过来的订单号
            if (payMap.get("paystate").equals("success!") && !payMap.get("out_trade_no").equals("")) {
                //交易金额
                String amount = payMap.get("amount").toString();
                paymentService.updatePayBack(payMap.get("out_trade_no").toString(), payMap.get("trade_no").toString(), "99bill", amount);
            }
        }
        //获取快钱POST过来反馈信息 支付成功success 支付失败fail
        return "<result>" + payMap.get("rtnOk") + "</result><redirecturl>=" + tUserSettingService.get().getSiteUrl()
                + "/billpayment/paybillfront?msg=" + payMap.get("paystate") + "</redirecturl>";
    }

}
