package com.framework.loippi.vo.store;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 功能： 商店订单成交统计
 * 类名：StoreStatisticsVo
 * 日期：2018/4/10  16:02
 * 作者：czl
 * 详细说明：
 * 修改备注:
 */
@Data
public class StoreStatisticsVo {

    /**
     * 店铺编号
     */
    private Long storeId;

    /**
     * 店铺名称
     */
    private String storeName;

    /**
     * 成交订单数↑↓
     */
    private Integer orderNum;

    /**
     * 成交金额↑
     */
    private BigDecimal orderPriceSum;

    /**
     * 成交商品数量↑↓
     */
    private Integer goodsNum;
}
