package com.framework.loippi.controller;

import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.utils.JacksonUtil;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by longbh on 16/7/28.
 */
public class BaseController {

    @Value("#{properties['img.server']}")
    protected String prefix;
    @Value("#{properties['wap.server']}")
    protected String wapServer;
    @Value("#{properties['api.server']}")
    protected String server;

    @Resource
    protected TwiterIdService twiterIdService;

    @ExceptionHandler({StateResult.class, Exception.class})
    @ResponseBody()
    public String Exception(Exception e) {
        StateResult stateResult;
        Map<String, Object> map = new HashMap<String, Object>();
        if (e instanceof StateResult) {
            stateResult = (StateResult) e;
            map.put("code", stateResult.code);
            map.put("message", stateResult.message);
            if (stateResult.data == null) {
                map.put("data", Lists.newArrayList());
            } else {
                map.put("data", stateResult.data);
            }
        } else {
            map.put("code", AppConstants.FAIL);
            map.put("message", e.getMessage());
        }
        e.printStackTrace();
        return JacksonUtil.toJson(map);
    }

    protected String jsonSucess() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("message", "成功");
        return JacksonUtil.toJson(result);
    }

    protected String jsonSucess(Object data) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("data", data);
        result.put("message", "成功");
        return JacksonUtil.toJson(result);
    }

    protected String jsonFail() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("message", "失败");
        return JacksonUtil.toJson(result);
    }

    protected String jsonFail(int status) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", status);
        result.put("message", "失败");
        return JacksonUtil.toJson(result);
    }

    protected String jsonFail(String message) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 0);
        result.put("message", message);
        return JacksonUtil.toJson(result);
    }

}
