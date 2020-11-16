package com.framework.loippi.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.service.HttpService;
import com.framework.loippi.service.KuaidiService;

/**
 * Created by longbh on 2018/11/11.
 */
@Service
public class KuaidiServiceImpl implements KuaidiService {

    @Autowired
    private HttpService httpService;

    /**
     * 快递100提供的快递查询
     *
     * @param com 快递公司编码 详情参考快递100提供的编码
     * @param num 查询的快递单号
     */
    /*public String query(String com, String num) {
        String param = "{\"com\":\"" + com + "\",\"num\":\"" + num + "\",\"from\":\"\",\"to\":\"\"}";
        String customer = "DC81FF281FD0D0C81F2FC7DB4D8ABD5F";
        String key = "OJAeqAkF2287";
        String sign = MD5.encode(param + key + customer);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("param", param);
        params.put("sign", sign);
        params.put("customer", customer);
        String resp = "";
        try {
            resp = httpService.postForm("http://poll.kuaidi100.com/poll/query.do", params).toString();
            System.out.println(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resp;
    }*/

    /**
     * 阿里云提供的快递查询
     *
     * @param type 快递公司编码 详情参考
     * @param no 查询的快递单号
     */
    public String query(String type, String no) {

        String host = "https://kdwlcxf.market.alicloudapi.com";// 【1】请求地址 支持http 和 https 及 WEBSOCKET
        String path = "/kdwlcx";// 【2】后缀
        String appcode = "cdd765154d864df180bd6546e4ab16c0"; // 【3】开通服务后 买家中心-查看AppCode
         //no = ""; // 【4】请求参数，详见文档描述
         //type = "zto"; //  【4】请求参数，不知道可不填 95%能自动识别
        String urlSend = host + path + "?no=" + no + "&type=" + type; // 【5】拼接请求链接
        String json = "";
        try {
            URL url = new URL(urlSend);
            HttpURLConnection httpURLCon = (HttpURLConnection) url.openConnection();
            httpURLCon.setRequestProperty("Authorization", "APPCODE " + appcode);// 格式Authorization:APPCODE
            // (中间是英文空格)
            int httpCode = httpURLCon.getResponseCode();
            if (httpCode == 200) {
                json = read(httpURLCon.getInputStream());
                System.out.println("正常请求计费(其他均不计费)");
                System.out.println("获取返回的json:");
                System.out.print(json);
            } else {
                Map<String, List<String>> map = httpURLCon.getHeaderFields();
                String error = map.get("X-Ca-Error-Message").get(0);
                if (httpCode == 400 && error.equals("Invalid AppCode `not exists`")) {
                    System.out.println("AppCode错误 ");
                } else if (httpCode == 400 && error.equals("Invalid Url")) {
                    System.out.println("请求的 Method、Path 或者环境错误");
                } else if (httpCode == 400 && error.equals("Invalid Param Location")) {
                    System.out.println("参数错误");
                } else if (httpCode == 403 && error.equals("Unauthorized")) {
                    System.out.println("服务未被授权（或URL和Path不正确）");
                } else if (httpCode == 403 && error.equals("Quota Exhausted")) {
                    System.out.println("套餐包次数用完 ");
                } else {
                    System.out.println("参数名错误 或 其他错误");
                    System.out.println(error);
                }
            }

        } catch (MalformedURLException e) {
            System.out.println("URL格式错误");
        } catch (UnknownHostException e) {
            System.out.println("URL地址错误");
        } catch (Exception e) {
            // 打开注释查看详细报错异常信息
            // e.printStackTrace();
        }
        return json;
    }

    /*
     * 读取返回结果
     */
    private static String read(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = new BufferedReader(new InputStreamReader(is,"utf-8"));
        String line = null;
        while ((line = br.readLine()) != null) {
            line = new String(line.getBytes());
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

    /**
     * 根据物流单号查询相应物流公司
     *
     * @param text 快递单号
     */
    public String queryCom(String text) {
        String url = "http://www.kuaidi100.com/autonumber/auto?num=" + text + "&key=WAeyPuvM3824";
        String resp = "";
        try {
            resp = httpService.post(url, null).toString();
            System.out.println(resp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resp;
    }

}
