package com.framework.loippi.enus;

import java.util.HashMap;
import java.util.Map;

/**
 * 活动类型
 */
public class ActivityTypeEnus {

    public static final String FILE_BASEPATH = "./";
    public static final String ADMIN_SERVER = "http://119.29.157.217:8373//upload/img/store/0";
    public static final String STATIC_PAGE_BASEPATH = "";


    public static final Map<String, String> activitMap = new HashMap<String, String>();

    public static final int ZHUAN_CHANG = 1;

    static {
        activitMap.put("zhuanchang", "促销活动");
    }


    public static final Map<Integer, String> activitMapStr = new HashMap<Integer, String>();

    static {
        activitMapStr.put(1, "促销活动");
    }

    public static final Map<String, Integer> activitTypeMap = new HashMap<String, Integer>();

    static {
        activitTypeMap.put("zhuanchang", 1);
    }

    public static final Map<Integer, String> activitTypeEnumMap = new HashMap<Integer, String>();

    static {
        activitTypeEnumMap.put(1, "zhuanchang");
    }

    ///满减10/满免邮20/满送30/满折40/团购50/限时抢购60/70套装/80买多送一
    public enum EnumType {
        zhuanchang("zhuanchang", 1);
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
            for (EnumType c : EnumType.values()) {
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
