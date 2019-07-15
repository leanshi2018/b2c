package com.framework.loippi.utils;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import sun.misc.BASE64Encoder;

import com.framework.loippi.param.user.UserAddBankCardsParam;
import org.json.JSONObject;


/**
 * Created by dzm on 2018/12/27.
 */
public class BankCardUtils {
    public static String calcAuthorization(String source, String secretId, String secretKey, String datetime)
        throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        String signStr = "x-date: " + datetime + "\n" + "x-source: " + source;
        Mac mac = Mac.getInstance("HmacSHA1");
        Key sKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), mac.getAlgorithm());
        mac.init(sKey);
        byte[] hash = mac.doFinal(signStr.getBytes("UTF-8"));
        String sig = new BASE64Encoder().encode(hash);

        String auth = "hmac id=\"" + secretId + "\", algorithm=\"hmac-sha1\", headers=\"x-date x-source\", signature=\"" + sig + "\"";
        return auth;
    }

    public static String vaildBankCard(UserAddBankCardsParam param) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        //云市场分配的密钥Id
        String secretId = "AKID1o0r8s8FPb2bzbh6c2liae18w2h20c4adk72";
        //云市场分配的密钥Key
        String secretKey = "cn5d8q567v9p1x8mb6cc9rJp4poj6j5slkql5kke";
        String source = "market";

        Calendar cd = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String datetime = sdf.format(cd.getTime());
        // 签名
        String auth = calcAuthorization(source, secretId, secretKey, datetime);
        // 请求方法
        String method = "GET";
        // 请求头
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("X-Source", source);
        headers.put("X-Date", datetime);
        headers.put("Authorization", auth);

        // 查询参数
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("bankcard", param.getAccCode());
        queryParams.put("idcard", param.getIdCard());
        queryParams.put("mobile", param.getMobile());
        queryParams.put("name",param.getAccName());

        // body参数
        Map<String, String> bodyParams = new HashMap<String, String>();

        // url参数拼接
        String url = "https://service-m5ly0bzh-1256140209.ap-shanghai.apigateway.myqcloud.com/release/bank_card4/verify";
        if (!queryParams.isEmpty()) {
            url += "?" + paramsResult(queryParams);
        }
        String resultS = deGet(url,headers,null);
        JSONObject jsonObject = new JSONObject(resultS);
        String code=Optional.ofNullable(jsonObject.getString("code")).orElse("");
        if (code.equals("20010")){
            resultS=Optional.ofNullable(jsonObject.getString("message")).orElse("");
            return resultS;
        }
        String res=Optional.ofNullable(jsonObject.getJSONObject("result").getString("res")).orElse("");
        String description=Optional.ofNullable(jsonObject.getJSONObject("result").getString("description")).orElse("");
        if (code.equals("0") && res.equals("1")){
            resultS="1";
        }else if (code.equals("0") && res.equals("2")){
            resultS=description;
        }
        else{
            resultS=Optional.ofNullable(jsonObject.getString("message")).orElse("");
        }
        System.out.println(resultS);
        return resultS;

    }

    public static String paramsResult (Map<String, Object> params ) {
        String s = "";
        for (Map.Entry map : params.entrySet()) {
            if (s != "") {
                s = s + "&";
            }
            s = s + map.getKey() + "=" + map.getValue();
        }
        return s;
    }

    public static String deGet (String uri,Map<String, String> headers,Map<String,Object> data) {
        CloseableHttpClient client = HttpClients.createDefault();
        List<NameValuePair> params = new ArrayList();
        if(data != null) {
            Iterator iterator = data.entrySet().iterator();
            while(iterator.hasNext()) {
                Map.Entry entry = (Map.Entry)iterator.next();
                params.add(new BasicNameValuePair(entry.getKey() + "", entry.getValue() + ""));
            }
        }
        try {
            HttpGet get = new HttpGet(uri);
            if(headers != null) {
                Iterator iterator1 = headers.entrySet().iterator();
                while(iterator1.hasNext()) {
                    Map.Entry<String, String> entry = (Map.Entry)iterator1.next();
                    get.addHeader((String)entry.getKey(), (String)entry.getValue());
                }
            }
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity, "UTF-8").trim();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
