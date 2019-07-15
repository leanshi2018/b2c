package com.framework.loippi.service.impl;

import com.framework.loippi.service.HttpService;
import com.framework.loippi.service.KuaidiService;
import com.framework.loippi.utils.MD5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

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
    public String query(String com, String num) {
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
