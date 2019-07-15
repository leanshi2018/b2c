package com.framework.loippi.enus;

import java.util.HashMap;
import java.util.Map;

/**
 * 活动规则类型
 */
public class
ActivityRuleTypeEnus {


    public static final int MAN_JIAN = 10;
    public static final int MAN_MIANYOU = 20;
    public static final int MAN_SONG = 30;
    public static final int MAN_ZHE = 40;
    public static final int MAN_REN_XUAN = 100;
    public static final int MAN_SONG_QUAN = 110;
    public static final int ZHE_KOU = 120;
    public static final int YI_KOU_JIA = 130;

    public static final Map<String, String> activitMap = new HashMap<String, String>();

    static {

        activitMap.put("manmianyou", "满免邮");
        activitMap.put("mansong", "满送");
        activitMap.put("duosongyi", "多送一");
        activitMap.put("manjian", "满减");
        activitMap.put("manzhe", "满折");
        activitMap.put("manrenxuan", "满任选");
        activitMap.put("mansongquan", "满送优惠券");
        activitMap.put("zhekou", "折扣");
        activitMap.put("yikoujia", "一口价");
    }


    public static final Map<Integer, String> activitMapStr = new HashMap<Integer, String>();

    static {
        activitMapStr.put(20, "满免邮");
        activitMapStr.put(30, "满送");
        activitMapStr.put(80, "多送一");
        activitMapStr.put(10, "满减");
        activitMapStr.put(40, "满折");
        activitMapStr.put(100, "满任选");
        activitMapStr.put(110, "满送优惠券");
        activitMapStr.put(120, "折扣");
        activitMapStr.put(130, "一口价");
    }

    public static final Map<String, Integer> activitTypeMap = new HashMap<String, Integer>();

    static {
        activitTypeMap.put("manjian", 10);
        activitTypeMap.put("manmianyou", 20);
        activitTypeMap.put("mansong", 30);
        activitTypeMap.put("duosongyi", 80);
        activitTypeMap.put("manzhe", 40);
        activitTypeMap.put("manrenxuan", 100);
        activitTypeMap.put("mansongquan", 110);
        activitTypeMap.put("zhekou", 120);
        activitTypeMap.put("yikoujia", 130);
    }

    public static final Map<Integer, String> activitTypeEnumMap = new HashMap<Integer, String>();

    static {
        activitTypeEnumMap.put(20, "manmianyou");
        activitTypeEnumMap.put(30, "mansong");
        activitTypeEnumMap.put(80, "duosongyi");
        activitTypeEnumMap.put(10, "manjian");
        activitTypeEnumMap.put(40, "manzhe");
        activitTypeEnumMap.put(100, "manrenxuan");
        activitTypeEnumMap.put(110, "mansongquan");
        activitTypeEnumMap.put(120, "zhekou");
        activitTypeEnumMap.put(130, "yikoujia");
    }

    ///满减10/满免邮20/满送30/满折40/团购50/限时抢购60/70套装/80买多送一
    public enum EnumType {
        manjian("manjian", 10), manmianyou("manmianyou", 20), mansong("mansong", 30),
        manzhe("manzhe", 40), tuangou("tuangou", 50), duosongyi("duosongyi", 80), manrenxuan("manrenxuan",
            100), mansongquan("mansongquan", 110), zhekou("zhekou", 120), yikoujia("yikoujia", 130);

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
