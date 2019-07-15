package com.framework.loippi.utils.wechat.mobile.util;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.framework.loippi.entity.Pay;
import com.framework.loippi.utils.wechat.mobile.config.WXpayConfig;
import lombok.extern.slf4j.Slf4j;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.tencent.mm.sdk.modelpay.PayReq;

/**
 * 微信签名工具
 *
 * @author 谢进伟
 * @company 雷铭智信
 * @DateTime 2015-3-22 下午9:16:39
 */
@Slf4j
public class SignUtils {

    private PayReq req = null;// 支付请求对象
    private Map<String, String> resultunifiedorder;// 预支付返回参数集合
    //private final Logger log = Logger.getLogger(SignUtils.class);

    /**
     * 生成签名参数
     *
     * @param 待签名订单信息
     * @return
     */
    private void genPayReq() {
        req.appId = WXpayConfig.APP_ID;
        req.partnerId = WXpayConfig.MCH_ID;
        req.prepayId = resultunifiedorder.get("prepay_id");
        req.packageValue = "Sign=WXPay";// "prepay_id=" +
        // resultunifiedorder.get("prepay_id");
        req.nonceStr = genNonceStr();
        req.timeStamp = String.valueOf(genTimeStamp());
        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("package", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
        req.sign = genAppSign(signParams);
    }

    /**
     * 接收预支付返回参数
     *
     * @param result
     */
    private void onPostExecute(Map<String, String> result) {
        resultunifiedorder = result;
    }

    /**
     * 解析预支付响应结果
     *
     * @param content 预支付响应结果
     * @return
     */
    private Map<String, String> decodeXml(String content) {
        try {
            Map<String, String> xml = new HashMap<String, String>();
            XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
            parser.setInput(new StringReader(content));
            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String nodeName = parser.getName();
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        if ("xml".equals(nodeName) == false) {
                            xml.put(nodeName, parser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }
            return xml;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将生成的商品参数组合换成了个微信支付指定的格式
     *
     * @param params 已生成的商品参数
     * @return
     */
    private String genPackageSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(WXpayConfig.API_KEY);
        String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
        return packageSign;
    }

    /**
     * 将预支付参数进行签名
     *
     * @param params 预支付参数
     * @return
     */
    private String genAppSign(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            sb.append(params.get(i).getName());
            sb.append('=');
            sb.append(params.get(i).getValue());
            System.out.println(params.get(i).getName() + "==>" + params.get(i).getValue());
            sb.append('&');
        }
        sb.append("key=");
        sb.append(WXpayConfig.API_KEY);
        String appSign = MD5.getMessageDigest(sb.toString().getBytes());
        return appSign;
    }

    /**
     * 将参数转换为xml形式
     *
     * @param params 参数
     * @return
     */
    private String toXml(List<NameValuePair> params) {
        StringBuilder sb = new StringBuilder();
        sb.append("<xml>");
        for (int i = 0; i < params.size(); i++) {
            sb.append("<" + params.get(i).getName() + ">");
            sb.append(params.get(i).getValue());
            sb.append("</" + params.get(i).getName() + ">");
        }
        sb.append("</xml>");
        return sb.toString();
    }

    /**
     * 生成随机字符串
     *
     * @return
     */
    private String genNonceStr() {
        Random random = new Random();
        return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
    }

    /**
     * 生成时间戳
     *
     * @return
     */
    private long genTimeStamp() {
        return System.currentTimeMillis() / 1000;
    }

//	public static void main(String[] args) {
//		SignUtils df=new SignUtils();
//		OrderPay order=new OrderPay();
//		order.setPaySn(System.nanoTime()+"");
//		order.setPayAmount(new BigDecimal("0.01"));
//		Map<String, Object> map = df.advancePayment(order,false);
//		Iterator<String> iterator = map.keySet().iterator();
//		while(iterator.hasNext()){
//			String key = iterator.next();
//			System.out.println(key+"----"+map.get(key));
//		}
//	}
}
