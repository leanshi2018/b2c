package com.framework.loippi.service;

import java.util.Map;

/**
 * Created by longbh on 2017/1/3.
 */
public interface HttpService {

    /**
     * post请求
     * @param postBody
     * @return
     */
    String post(String url, String postBody);

    /**
     * 表单提交
     * @param url
     * @param form
     * @return
     */
    String postForm(String url, Map<String, String> form);

}
