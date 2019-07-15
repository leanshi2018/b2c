package com.framework.loippi.enus;

import java.util.HashMap;
import java.util.Map;

/**
 * 活动商品关联表类型
 */
public class
ActivityObjectTypeEnus {


    //10 规格 20 商品 30 商品分类 40 商品类型 50 商品品牌  60 活动
    public enum EnumType {
        specifications("specifications", 10), goods("goods", 20), goodsclass("goodsclass", 30),
        goodstype("goodstype", 40), brand("brand", 50), activity("activity", 60);

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
