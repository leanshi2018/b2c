package com.framework.loippi.result.app.cart;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.framework.loippi.consts.GoodsState;
import com.framework.loippi.entity.cart.ShopCart;
import com.framework.loippi.entity.cart.ShopCartExchange;
import com.framework.loippi.vo.cart.ShopCartExchangeVo;
import com.framework.loippi.vo.cart.ShopCartVo;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Data
@Accessors(chain = true)
public class CartExchangeResult {
    /**
     * 品牌id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long brandId;

    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 品牌图标
     */
    private String brandIcon;
    /**
     * 购物车商品项
     */
    private List<CartItemResult> cartItems;

    /**
     * 商品种类数量
     */
    private Integer goodsTypeNum;

    /**
     * 满足包邮的条件金额
     */
    private BigDecimal shippingCouponAmount;


    public static List<CartExchangeResult> buildList(List<ShopCartExchangeVo> shopCarts, BigDecimal freightAmount) {
        if (CollectionUtils.isEmpty(shopCarts)) {
            return Lists.newArrayList();
        }

        // 根据品牌, 将购物车分组
        List<CartExchangeResult> cartResultList = Lists.newArrayList();
        for (ShopCartExchangeVo cart : shopCarts) {
            // 是否为失效商品（下架或删除）
            boolean isInvalid = cart.getGoods() != null &&
                    cart.getGoods().getState() == GoodsState.GOODS_OPEN_STATE &&
                    cart.getGoods().getGoodsShow() != null &&
                    cart.getGoods().getGoodsShow() == GoodsState.GOODS_ON_SHOW &&
                    cart.getGoods().getIsDel() != null &&
                    cart.getGoods().getIsDel() == GoodsState.GOODS_NOT_DELETE;
            if (!isInvalid || cart.getGoodsSpec() == null) {
                // 失效商品商店id
                cart.setGoodsId(-1L);

            }


            // cartResultList首个添加
            if (cartResultList.size() == 0) {
                cartResultList.add(CartExchangeResult.build(cart,freightAmount));
            } else {
                // 是否cart的商店id存在cartResultList中
                boolean flag = false;
                for (CartExchangeResult result : cartResultList) {
                    // cartResultList中存在cart商店id, 将cart加入
                    if (result.getBrandId().equals(cart.getBrandId())) {
                        result.getCartItems().add(CartItemResult.build2(cart));
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    cartResultList.add(CartExchangeResult.build(cart, freightAmount));
                }
            }
        }

        return cartResultList;
    }

    public static CartExchangeResult build(ShopCartExchangeVo cart, BigDecimal freightAmount) {
        Optional<ShopCartExchangeVo> optCart = Optional.ofNullable(cart);
        CartExchangeResult cartExchangeResult = new CartExchangeResult()
                .setBrandId(optCart.map(ShopCartExchangeVo::getBrandId).orElse(-1L))
                .setBrandName(optCart.map(ShopCartExchangeVo::getBrandName).orElse("失效品牌"))
                .setBrandIcon(optCart.map(ShopCartExchangeVo::getBrandIcon).orElse(""))
                .setCartItems(Lists.newArrayList(CartItemResult.build2(cart)));
        cartExchangeResult.setShippingCouponAmount(freightAmount);
        cartExchangeResult.setGoodsTypeNum(cartExchangeResult.getCartItems().size());
        return cartExchangeResult;
    }
}
