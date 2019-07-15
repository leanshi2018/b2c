package com.framework.loippi.utils.jpush;

import java.util.Map;

public class JpushRunnableSecond extends Thread {

    private String message;
    private String[] alias;
    private Map<String, String> extras;

    public JpushRunnableSecond(String message, String[] alias) {
        this.message = message;
        this.alias = alias;
    }

    public JpushRunnableSecond(String message, String alias[], Map<String, String> extras) {
        this.message = message;
        this.alias = alias;
        this.extras = extras;
    }

    public void run() {
        //极光推送信息
        if (extras == null || extras.size() == 0) {
            String msgId = JpushUtilsSecond.push2alias(message, alias);
        } else {
            String msgId = JpushUtilsSecond.push2alias(message, alias, extras, 0);
        }
    }
}

