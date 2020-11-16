package com.framework.loippi.controller;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by longbh on 2017/7/28.
 */
@Controller
@Slf4j
@RequestMapping("/commons")
public class CommonController extends GenericController {

    @RequestMapping(value = {"/show_msg"}, method = {RequestMethod.GET})
    public String msgShow(String msg, ModelMap modelMap) {
        modelMap.addAttribute("msg", msg);
        return "/common/common/show_msg";
    }

    /*@RequestMapping("/kuaidi/aliShipping")
    @ResponseBody
    public String queryWlInfo()
    {

        String host = "https://kdwlcxf.market.alicloudapi.com";// 【1】请求地址 支持http 和 https 及 WEBSOCKET
        String path = "/kdwlcx";// 【2】后缀
        String appcode = "cdd765154d864df180bd6546e4ab16c0"; // 【3】开通服务后 买家中心-查看AppCode
        String no = "YT5008038329386"; // 【4】请求参数，详见文档描述
        String type = ""; //  【4】请求参数，不知道可不填 95%能自动识别
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

        Map mapType = JacksonUtil.convertMap(json);
        Map<String, List<Map<String,String>>> result = (Map<String, List<Map<String,String>>>) mapType.get("result");
        List<Map<String, String>> datainfo = result.get("list");
        System.out.println("info="+datainfo);
        //是否存在物流信息
        if (datainfo != null) {
            Map<String, String> map=new HashMap<>();
            //如果存在物流信息 则显示有信息的那一条
            Integer num = 1;
            for (Map<String, String> item :datainfo) {
                if (item!=null){
                    if (num==1){
                        map=item;
                        num++;
                    }
                }
            }
            System.out.println("map="+map);
        } else {
        }

        return "";
    }*/

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

}
