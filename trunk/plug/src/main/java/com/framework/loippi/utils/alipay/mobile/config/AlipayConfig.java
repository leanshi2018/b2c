package com.framework.loippi.utils.alipay.mobile.config;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/* *
 * 类名：AlipayConfig
 * 功能：基础配置类
 * 详细：设置帐户有关信息及返回路径
 * 版本：3.3
 * 日期：2012-08-10
 * 说明：
 * 以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 * 该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 * 
 * 提示：如何获取安全校验码和合作身份者ID
 * 1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 * 2.点击“商家服务”(https://b.alipay.com/order/myOrder.htm)
 * 3.点击“查询合作者身份(PID)”、“查询安全校验码(Key)”
 * 
 * 安全校验码查看时，输入支付密码后，页面呈灰色的现象，怎么办？
 * 解决方法：
 * 1、检查浏览器配置，不让浏览器做弹框屏蔽设置
 * 2、更换浏览器或电脑，重新登录查询。
 */
@Slf4j
public class AlipayConfig {

    ///**
    // * 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
    // */
    //public static String RETURNURL = "m.alipay.com";
    ///**
    // * 设置未付款交易的超时时间 默认30分钟，一旦超时，该笔交易就会自动被关闭。 取值范围：1m～15d。
    // * m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点，如1.5h，可转换为90m。
    // */
    //public static String ITBPAY = "";
    //// ↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    ///**
    // * 合作身份者ID，以2088开头由16位纯数字组成的字符串
    // */
    //public static String PARTNER = "";
    //
    ///**
    // * 商户的私钥
    // */
    //public static String PRIVATE_KEY = "";
    //
    ///**
    // * 支付宝的公钥，无需修改该值
    // */
    //public static String ALI_PUBLIC_KEY = "";
    //
    //// ↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
    //
    /**
     * 调试用，创建TXT日志文件夹路径
     */
    public static String LOG_PATH = "./";
    //
    ///**
    // * 字符编码格式 目前支持 gbk 或 utf-8
    // */
    //public static String INPUT_CHARSET = "utf-8";
    //
    ///**
    // * 签名方式 不需修改
    // */
    //public static String SIGN_TYPE = "RSA";
    //
    ///**
    // * 卖家支付宝账号
    // */
    //public static String SELLERID = "";
    //
    //
    //public static String APPID = "";
    //
    ///**
    // * 初始化支付宝支付参数
    // */
    //public static void initAlipayConfig(Map<String, Object> dto) {
    //    AlipayConfig.RETURNURL = "m.alipay.com";
    //    AlipayConfig.ITBPAY = "30m";
    //    AlipayConfig.PARTNER = dto.get("partner") + "";
    //    AlipayConfig.PRIVATE_KEY = dto.get("private_key") + "";
    //    AlipayConfig.ALI_PUBLIC_KEY = dto.get("public_key") + "";
    //    AlipayConfig.SELLERID = dto.get("sellerId") + "";
    //    AlipayConfig.APPID = dto.get("appId") + "";
    //}
    /**
     * 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
     */
    public static String RETURNURL = "m.alipay.com";
    /**
     * 设置未付款交易的超时时间 默认30分钟，一旦超时，该笔交易就会自动被关闭。 取值范围：1m～15d。
     * m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。 该参数数值不接受小数点，如1.5h，可转换为90m。
     */
    public static String ITBPAY = "";

    //↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    // 合作身份者ID，以2088开头由16位纯数字组成的字符串
    public static String partner = "2088131400635800";
    // 商户的私钥
    public static String private_key = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCN1C3Ut7DypMDjfYxzbgc5qtitEgZHM5a2k4+d75YuRRZDAkW6nWLa8Zg31yH/Ysua/wv3l3LC/JpKNo7u9k+4jKDaS2hnVZ6HEUsPoSXyaLAqt6bYT7OiUe3mlsem2WeuW7N/Th1rs5A1Zly/hj4MBK9048sYXgXPuXUOyvdpx7EWjKRox+HtUqa3vh7NHYWIWMw9fJza8xBI6rsltrq7Uc0waoots69as+riKRo3n1JqNCa4BBs3Zt1Rnhho2HG+RS0zUUVc/fAFzsCf+9BWt0FiWAQ5japiu/Zdi9g0QQz05kfQPZSaYfNN7rkSG3PyivyQcpCKtwRYtFquLfBRAgMBAAECggEAeEN3KW0p6layCTUop6ihD9FDMl6aXD3uiprNqUrDujBzFBcVMYzLioRaYt9VwMzxgVjLuQL/GUBBRLTGtvK93uvAadqPiTAc4MNVNsea+dazq0VXgQz0cauvY1xvp5eqRzsTFdwTLoRwwBcwhmc6rc47SY7vZJY1r1K/Y+/PpC2e9S2TejPn1Ye0TxvfjXRUgscPzwmbPnCV8O41JxNYDI/TPZcVCOqG9kHevT6qT8Zt+LQwXJ2MNdyWMtAslFh8F3cT3NhwWHwvuOX4fy/8aPuZVAqpR9TDxqHR59a0KetsAyMjCfKNbAv0T+KI17pixAL23OelqOziJgyn5rpIAQKBgQDXWLJOPpI1vu3ADy+x3sR3v+qe9oonsFEGjAAEwTwzUHXcFjTHXJVX1d9Jk944X510nt1PSouqYOt+CDxzZmCSqTQZqGU2EJXEE4oYnqfW0VsXxX4rXK2VlmtvDM2J0LJqsPT7cXWKldjMXXp0cbpzkPOBVYVJo4YG23rFgCRm8QKBgQComoIgLRZIZRYVCyM1JdSrIZ3oey4J4xCmnVVDeohtNx40XwW/bSgSV41/wVKtvZ330Qqf2XWsSNoQ4mvfxP4NL4yqkSYdxwh7TfNs9hEy4NkHUpLX8sshz12zeefNgsFVtbGNBiRzf2qB0dpdv/cvTXZDdCE1pziayHdNfCffYQKBgEES0G7Mgao0E+whqqv5QS1elSuMHJt5sEJmM+s76VEoUzU6kKBd3dM5EcxOHqyyWAZRq1a2lBXcS8099+Z3XV90OgY22L7JrDggH27pIpbLlNdOGp5H1zyIw9Y+7C1lH0UWTDFfS8GhHm5DISnm5AzgFHVUvn5OJ8YStLrRyi5BAoGAKC1RbttvoOa9wVv7EtImAHWtTFX2HZMHR7zcMY1Eb0ogH3i5ROl9EAnfBR+LVS2HNCOTNY47VUey9y0NabBmnbvD+G1IOPZR1hg4NrHYXVhpPy51IZKQx0jD2l3lV/qdmZAHdESPcRpexfq833Q8MgByexZ0R3XGrlOw4j0Av2ECgYB7NfTeI1n/nKcnh5jMtqcs55tF5wBvxBDzADzvHfIll/eu1YcoalsVbIzhl3OXQuN9jYNDAzB5WtJLVdal9Vwosmq5Hxcf35BKX3DS9Pt5cDlrr9ewmmyGslWpHTgHCbmkP1E6PVpnKOm1jVlpiGLVCKEEi1YVczPO9bYFTG6ZjA==";

    // 支付宝的公钥，无需修改该值
    public static String ali_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqNU0j/GEjqQ20jVEOObBEQn1M/1+REv3CsS47SaA458Eu94qrgEeEn1sPiIcNh/zKfWwF2u+kiw2Rte28YqRFOaMlnjBP1iWAindlIComCGCJ/qFebwF/OUZRSarLo1XIyghpYKivmqJZcIuAlwWHEd3+64B5uyz5Css8in3kfwoM+oL+urfwkD8vzcb9p7zUyliNCUjUmPPFgtyES6BOcdJE5sSrQSyxOb5C3tLVj7hqyRIRWZEfxqz5q9/fzxtAqqvzFlyfXAGt93nHmF5ohW0aYl7hiHC8f01cKjzU+tGCUYOZCyynNx9SVUNM0eIyM0kqQ4JF+vsJWIeZ9CCNwIDAQAB";

    //↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑


    // 调试用，创建TXT日志文件夹路径
    public static String log_path = "D:\\";

    // 字符编码格式 目前支持 gbk 或 utf-8
    public static String    input_charset = "utf-8";

    // 签名方式 不需修改
    public static String sign_type = "RSA2";

    /**
     * 卖家支付宝账号
     */
    public static String SELLERID = "";


    public static String APPID = "2018062560444084";

    //
    public static String format = "json";

    //(官网的支付宝网关）
    public static final  String gatewayUrl = "https://openapi.alipay.com/gateway.do";

    /**
     * 初始化支付宝支付参数
     */
    public static void initAlipayConfig(Map<String, Object> dto) {
        AlipayConfig.RETURNURL = "m.alipay.com";
        AlipayConfig.ITBPAY = "30m";
        AlipayConfig.partner = dto.get("partner") + "";
        AlipayConfig.private_key = dto.get("private_key") + "";
        AlipayConfig.ali_public_key = dto.get("public_key") + "";
        AlipayConfig.SELLERID = dto.get("sellerId") + "";
        AlipayConfig.APPID = dto.get("appId") + "";
    }
}
