package com.framework.loippi.enus;

import java.util.HashMap;
import java.util.Map;

/**
 * 分组类型   1商品分组  2团购分组  3促销商品分组
 */
public class ShopGroupTypeEnus {

    public static final Map<String, Integer> shopGroupTypeMap = new HashMap<String, Integer>();

    static {
        shopGroupTypeMap.put("shopgroup", 1);
        shopGroupTypeMap.put("tuangougroup", 2);
        shopGroupTypeMap.put("chuxiaogroup", 3);
    }

    public static final Map<Integer, String> shopGroupTypeEnumMap = new HashMap<Integer, String>();

    static {
        shopGroupTypeEnumMap.put(1, "shopgroup");
        shopGroupTypeEnumMap.put(2, "tuangougroup");
        shopGroupTypeEnumMap.put(3, "chuxiaogroup");
    }

     //1商品分组  2团购分组  3促销商品分组
    public enum EnumType {
         shopgroup("shopgroup", 1),
         tuangougroup("tuangougroup", 2),
         chuxiaogroup("chuxiaogroup", 3);
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
