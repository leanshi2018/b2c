package com.framework.loippi.pojo.cart;

import com.framework.loippi.entity.cart.ShopCartExchange;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Data
@ToString
public class CartExchangeInfo {
    /**
     * 品牌id
     */
    private Long brandId;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 购物车集合
     */
    private List<ShopCartExchange> list = Lists.newArrayList();
    /**
     * 商品总数量
     */
    private int goodsNum;
    /**
     * 商品总价格
     */
    private BigDecimal goodsTotalPrice;
    /**
     * 运费
     */
    public BigDecimal freightAmount;
    /**
     * 优惠运费
     */
    public BigDecimal preferentialFreightAmount;
    /**
     * 总价格
     */
    public BigDecimal totalPrice;
}
