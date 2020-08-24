package com.framework.loippi.consts;

import java.math.BigDecimal;

/**
 * 凑单区查询参数常量类
 */
public interface GatherAreaConstant {
    /**
     * 白菜价
     */
    public static final BigDecimal CABBAGE_PRICE=new BigDecimal("100.00");

    /**
     * 百元精选价格范围区间下
     */
    public static final BigDecimal HANDPICK_CLOSE_HUNDRED_LOW=new BigDecimal("80.00");

    /**
     * 百元精选价格范围区间上
     */
    public static final BigDecimal HANDPICK_CLOSE_HUNDRED_HIGH=new BigDecimal("199.00");

    /**
     * 包邮专区价格范围区间下
     */
    public static final BigDecimal PACKAGE_MAIL_LOW=new BigDecimal("199.00");

    /**
     * 包邮专区价格范围区间上
     */
    public static final BigDecimal PACKAGE_MAIL_HIGH=new BigDecimal("360.00");

    /**
     * 复消mi值
     */
    public static final BigDecimal AFTER_ELIMINATION_MI=new BigDecimal("25.00");
}
