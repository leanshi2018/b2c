package com.framework.loippi.vo.cart;

import com.framework.loippi.entity.cart.ShopCartExchange;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import lombok.Data;

@Data
public class ShopCartExchangeVo extends ShopCartExchange {
    /**
     * 购物车商品详
     */
    private ShopGoods goods;

    /**
     * 商品规格详情
     */
    private ShopGoodsSpec goodsSpec;
}
