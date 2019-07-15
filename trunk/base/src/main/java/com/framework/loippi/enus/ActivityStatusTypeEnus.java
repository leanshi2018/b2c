package com.framework.loippi.enus;

import java.util.HashMap;
import java.util.Map;

/**
 * 活动状态 活动状态  10  未开始  20 活动中 30已结束  40商品已销售完
 */
public class ActivityStatusTypeEnus {

    public enum EnumType {
        unstarted("unstarted", 10),
        inactivity("inactivity", 20),
        outstock("outstock",40),
        ended("ended", 30),;

        // 成员变量
        private String name;

        private int value;

        // 构造方法
        private EnumType(String name, int value) {
            this.name = name;
            this.value = value;
        }

        // 普通方法
        public static int getValue(String name) {
            for (ActivityStatusTypeEnus.EnumType c : ActivityStatusTypeEnus.EnumType.values()) {
                if (c.getName().equals(name)) {
                    return c.value;
                }
            }
            return 0;
        }

        // get set 方法
        public String getName() {
            return name;
        }

        public int getValue() {
            return value;
        }
    }
}
