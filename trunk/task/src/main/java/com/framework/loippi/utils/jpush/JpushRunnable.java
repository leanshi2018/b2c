package com.framework.loippi.utils.jpush;

import java.util.Map;

public class JpushRunnable extends Thread {

    private String message;
    private String[] alias;
    private Map<String, String> extras;

    public JpushRunnable(String message, String[] alias) {
        this.message = message;
        this.alias = alias;
    }

    public JpushRunnable(String message, String alias[], Map<String, String> extras) {
        this.message = message;
        this.alias = alias;
        this.extras = extras;
    }

    public void run() {
        //极光推送信息
        if (extras == null || extras.size() == 0) {
            String msgId = JpushUtils.push2alias(message, alias);
        } else {
            String msgId = JpushUtils.push2alias(message, alias, extras, 0);
        }
    }
}

