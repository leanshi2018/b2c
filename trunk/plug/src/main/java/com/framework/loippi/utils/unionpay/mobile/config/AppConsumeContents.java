package com.framework.loippi.utils.unionpay.mobile.config;

import java.io.InputStream;
import java.util.Map;

public class AppConsumeContents {
    /**
     * 银联
     */
    public static final String currencyCode = "156";//交易币种

    public static String merId = "";//商户号码，请改成自己的商户号
    public static final String txnSubType = "01";////交易子类型 01:自助消费 02:订购 03:分期付款
    public static final String version = "5.0.0";//版本号
    public static final String signMethod = "01";//签名方法 01
    //public static final String notifyUrl_topUp= propertiesLoader.getProperty("notifyUrl_topUp");//充值后台通知地址
    public static final String encoding = "UTF-8";//字符集编码 默认"UTF-8"
    public static final String reqReserved = "UTF-8";//字符集编码 默认"UTF-8"

    public static final String txnType = "01";//请求方保留域，透传字段，查询、通知、对账文件中均会原样出现
    public static final String bizType = "000201";//业务类型 000201 B2C网关支付
    public static final String accessType = "0";//接入类型:商户接入填0 0- 商户 ， 1： 收单， 2：平台商户
    //public static final String certPath= propertiesLoader.getProperty("certPath");//签名证书路径 （联系运营获取两码，在CFCA网站下载后配置，自行设置证书密码并配置）
    //public static final String certPath= SpringContextUtil.getResourceRootRealPath()+ File.separator+"com/leimingtech/extend/module/payment/unionpay/unionpayprokey/mobile/leiming.pfx";
//    public static final InputStream certPath=Thread.currentThread().getContextClassLoader().getResource("").toString().replaceFirst("file:/","")+"unionpayprokey/mobile/leiming.pfx";
    public final InputStream certPath = this.getClass().getClassLoader().getResourceAsStream("gzds.pfx");
    public static String certPwd = "968776";//签名证书密码

    /**
     * 初始化支付宝支付参数
     */
    public static void initConfig(Map<String, Object> dto) {
        AppConsumeContents.merId = dto.get("sellerId") + "";
    }
}
