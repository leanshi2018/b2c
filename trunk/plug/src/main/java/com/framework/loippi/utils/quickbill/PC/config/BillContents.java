package com.framework.loippi.utils.quickbill.PC.config;


import java.util.Map;

/**
 * 快钱
 */
public class BillContents {
    //人民币网关账户号;请登录快钱系统获取用户编号，用户编号后加01即为人民币网关账户号。
    public static String merchantAcctId;
    //人民币网关密钥;#区分大小写.请与快钱联系索取
    public static String key = "";
    //字符集.固定选择值。可为空;只能选择1、2、3;1代表UTF-8; 2代表GBK; 3代表gb2312;默认值为1;
    public static final String inputCharset = "1";
    //网关版本.固定值;快钱会根据版本号来调用对应的接口处理程序;本代码版本号固定为v2.0
    public static final String version = "2.0";
    //语言种类.固定选择值;只能选择1、2、3;1代表中文；2代表英文;默认值为1
    public static final String language = "1";
    //签名类型.固定值;1代表MD5签名;当前版本固定为1
    public static final String signType = "1";

    public static void initBillConfig(Map<String, Object> params) {
        BillContents.merchantAcctId = params.get("merchantAcctId") + "";
        BillContents.key = params.get("key") + "";
    }
}
