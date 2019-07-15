package com.framework.loippi.utils.unionpay.pc.gwj.vo;

import com.framework.loippi.utils.unionpay.pc.gwj.util.JsonUtil;

import java.util.HashMap;
import java.util.Map;

public class BaseVo {

    private String v;

    private String cmd;

    private Map<String, Object> params = new HashMap<String, Object>();

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String toString() {
        return JsonUtil.toJson(this);
    }

}
