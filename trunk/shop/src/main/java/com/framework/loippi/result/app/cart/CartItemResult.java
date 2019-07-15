package com.framework.loippi.result.app.cart;

import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.vo.cart.ShopCartVo;

import java.math.BigDecimal;
import java.util.Optional;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 功能： 购物车项数据
 * 类名：BaseGoodsResult
 * 日期：2017/8/7  14:22
 * 作者：czl
 * 详细说明：
 * 修改备注:
 */
@Data
@Accessors(chain = true)
public class CartItemResult extends BaseGoodsResult {



    /**
     * pv值
     */
    private BigDecimal ppv;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 购物车id
     */
    private Long cartId;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 活动类型
     */
    private Integer activityType;
    /**
     * 商品类型
     */
    private Integer goodsType;

    public static CartItemResult build(ShopCartVo cart) {
        Optional<ShopCartVo> optCart = Optional.ofNullable(cart);
        Optional<ShopGoodsSpec> optGoodsSpec = optCart.map(ShopCartVo::getGoodsSpec);
        CartItemResult itemResult = new CartItemResult();
        itemResult.setActivityId(optCart.map(ShopCartVo::getActivityId).orElse(0L));
        itemResult.setActivityType(optCart.map(ShopCartVo::getActivityType).orElse(0));
        itemResult.setCartId(optCart.map(ShopCartVo::getId).orElse(0L));
        itemResult.setGoodsMarketPrice(optCart.map(ShopCartVo::getGoodsRetailPrice).orElse(BigDecimal.ZERO));
        itemResult.setStock(optGoodsSpec.map(ShopGoodsSpec::getSpecGoodsStorage).orElse(0));
        itemResult.setGoodsType(optCart.map(ShopCartVo::getGoodsState).orElse(1));
        // 基本商品信息
        itemResult.setSpecInfo(optCart.map(ShopCartVo::getSpecInfo).orElse(""))
                .setGoodsName(optCart.map(ShopCartVo::getGoodsName).orElse(""))
//                .setGoodsStorePrice(optGoodsSpec.map(ShopGoodsSpec::getSpecRetailPrice).orElse(BigDecimal.ZERO))
                .setSpecId(optCart.map(ShopCartVo::getSpecId).orElse(0L))
                .setGoodsId(optCart.map(ShopCartVo::getGoodsId).orElse(0L))
                .setQuantity(optCart.map(ShopCartVo::getGoodsNum).orElse(0))
                .setDefaultImage(optCart.map(ShopCartVo::getGoodsImages).orElse(""));
        // TODO: 2018/12/11 待补充
        itemResult.setPpv(optGoodsSpec.map(ShopGoodsSpec::getPpv).orElse(BigDecimal.ZERO));
        return itemResult;
    }
}
