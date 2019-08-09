package com.framework.loippi.utils;

import sun.misc.BASE64Encoder;

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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.framework.loippi.param.user.UserAddBankCardsParam;


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
        //String url = "https://service-m5ly0bzh-1256140209.ap-shanghai.apigateway.myqcloud.com/release/bank_card4/verify";
        String url = "https://service-repo3dsb-1256140209.ap-shanghai.apigateway.myqcloud.com/release/bankcard4_c/get";
        if (!queryParams.isEmpty()) {
            url += "?" + urlencode(queryParams);
        }

        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod(method);

            // request headers
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                conn.setRequestProperty(entry.getKey(), entry.getValue());
            }

            // request body
            Map<String, Boolean> methods = new HashMap<>();
            methods.put("POST", true);
            methods.put("PUT", true);
            methods.put("PATCH", true);
            Boolean hasBody = methods.get(method);
            if (hasBody != null) {
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                conn.setDoOutput(true);
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.writeBytes(urlencode(bodyParams));
                out.flush();
                out.close();
            }

            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line;
            //String result = "";
            while ((line = in.readLine()) != null) {
                result += line;
            }

            System.out.println(result);
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        //return result;


        //String resultS = deGet(url,headers,null);
        //{"msg":"","success":true,"code":200,"data":{"result":0,"order_no":"609328563237294080","desc":"认证信息匹配","msg":"一致"}}
        //{"msg":"","success":true,"code":200,"data":{"result":1,"order_no":"609328326196203520","desc":"认证信息不匹配","msg":"不一致"}}
        JSONObject jsonObject = new JSONObject(result);
        Map maps = (Map) JSON.parse(jsonObject.toString());

        int code = (int)maps.get("code");
        System.out.println("code="+code);
        if (code==400){
            result=(String)maps.get("msg");
            return result;
        }
        com.alibaba.fastjson.JSONObject data=(com.alibaba.fastjson.JSONObject)maps.get("data");
        System.out.println("data="+data);
        Map mapDate = (Map) JSON.parse(data.toString());
        int res = (int)mapDate.get("result");
        String desc = (String)mapDate.get("desc");
        String msg = (String)mapDate.get("msg");
        if (code==200&&res==0){
            result="1";
        }else{
            result=desc+","+msg;
        }
        System.out.println(result);
        return result;

    }

    public static String urlencode(Map<?, ?> map) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(String.format("%s=%s",
                    URLEncoder.encode(entry.getKey().toString(), "UTF-8"),
                    URLEncoder.encode(entry.getValue().toString(), "UTF-8")
            ));
        }
        return sb.toString();
    }



}
