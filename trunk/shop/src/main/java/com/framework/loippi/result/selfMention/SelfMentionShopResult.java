package com.framework.loippi.result.selfMention;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 自提小店返回实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelfMentionShopResult {
    /**
     * 商品种类数量
     */
    private Integer goodsTypeNum;

    /**
     * 今日订单数
     */
    private Integer dailyOrderNum;

    /**
     * 本月订单数
     */
    private Integer monthOrderNum;

    /**
     * 当月销售额
     */
    private BigDecimal monthSales;

    public static SelfMentionShopResult build(Integer goodsTypeNum, Integer dailyNum, Integer monthNum, BigDecimal total) {
        SelfMentionShopResult result = new SelfMentionShopResult();
        result.setGoodsTypeNum(goodsTypeNum);
        result.setDailyOrderNum(dailyNum);
        result.setMonthOrderNum(monthNum);
        result.setMonthSales(total);
        return result;
    }
}
