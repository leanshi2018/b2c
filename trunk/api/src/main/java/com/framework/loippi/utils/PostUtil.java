package com.framework.loippi.utils;

import com.alibaba.druid.support.json.JSONUtils;
import com.rabbitmq.tools.json.JSONUtil;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PostUtil {

    public static String postRequest(String url, String oMCode, String password) throws Exception {
        URL restURL = new URL(url);
        /*
         * 此处的urlConnection对象实际上是根据URL的请求协议(此处是http)生成的URLConnection类 的子类HttpURLConnection
         */
        HttpURLConnection conn = (HttpURLConnection) restURL.openConnection();
        //请求方式
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Charsert", "UTF-8"); //设置请求编码
        conn.setRequestProperty("Content-Type", "application/json");
        //设置是否从httpUrlConnection读入，默认情况下是true; httpUrlConnection.setDoInput(true);
        conn.setDoOutput(true);
        //allowUserInteraction 如果为 true，则在允许用户交互（例如弹出一个验证对话框）的上下文中对此 URL 进行检查。
        conn.setAllowUserInteraction(false);
        Map<String, String> date = new HashMap<>();
        date.put("oMCode",oMCode);
        date.put("password",password);

        PrintStream ps = new PrintStream(conn.getOutputStream());
        ps.print(JSONUtils.toJSONString(date));

        ps.close();

        BufferedReader bReader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));

        String line = "";
        StringBuilder json = new StringBuilder();

        while (null != (line = bReader.readLine())) {
            json.append(line);
            //resultStr +=line;
        }
        System.out.println("3412412---" + json);
        bReader.close();
        //return resultStr;

        return json.toString();
    }
}
