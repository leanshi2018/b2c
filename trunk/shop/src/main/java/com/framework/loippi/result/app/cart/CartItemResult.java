package com.framework.loippi.result.app.cart;

import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.vo.cart.ShopCartExchangeVo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Optional;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.vo.cart.ShopCartVo;

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
     * 大单pv值
     */
    private BigDecimal bigPpv;

    /**
     * 库存
     */
    private Integer stock;

    /**
     * 购物车id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long cartId;

    /**
     * 活动id
     */
    @JsonSerialize(using = ToStringSerializer.class)
    private Long activityId;

    /**
     * 活动类型
     */
    private Integer activityType;
    /**
     * 商品类型
     */
    private Integer goodsType;
    /**
     * 会员价
     */
    private BigDecimal vipPrice;
    /**
     * 大单价
     */
    private BigDecimal bigPrice;
    //是否为plus vip商品 0：不是 1：是
    private Integer plusVipType;

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
        itemResult.setVipPrice(optGoodsSpec.map(ShopGoodsSpec::getSpecMemberPrice).orElse(BigDecimal.ZERO));
        itemResult.setBigPpv(optGoodsSpec.map(ShopGoodsSpec::getBigPpv).orElse(BigDecimal.ZERO));
        itemResult.setBigPrice(optGoodsSpec.map(ShopGoodsSpec::getSpecBigPrice).orElse(BigDecimal.ZERO));
        ShopGoods goods = cart.getGoods();
        if(goods!=null&&goods.getPlusVipType()!=null){
            itemResult.setPlusVipType(goods.getPlusVipType());
        }else {
            itemResult.setPlusVipType(0);
        }
        return itemResult;
    }

    public static CartItemResult build2(ShopCartExchangeVo cart) {
        Optional<ShopCartExchangeVo> optCart = Optional.ofNullable(cart);
        Optional<ShopGoodsSpec> optGoodsSpec = optCart.map(ShopCartExchangeVo::getGoodsSpec);
        CartItemResult itemResult = new CartItemResult();
        itemResult.setActivityId(optCart.map(ShopCartExchangeVo::getActivityId).orElse(0L));
        itemResult.setActivityType(optCart.map(ShopCartExchangeVo::getActivityType).orElse(0));
        itemResult.setCartId(optCart.map(ShopCartExchangeVo::getId).orElse(0L));
        itemResult.setGoodsMarketPrice(optCart.map(ShopCartExchangeVo::getGoodsRetailPrice).orElse(BigDecimal.ZERO));
        itemResult.setStock(optGoodsSpec.map(ShopGoodsSpec::getSpecGoodsStorage).orElse(0));
        itemResult.setGoodsType(optCart.map(ShopCartExchangeVo::getGoodsState).orElse(1));
        // 基本商品信息
        itemResult.setSpecInfo(optCart.map(ShopCartExchangeVo::getSpecInfo).orElse(""))
                .setGoodsName(optCart.map(ShopCartExchangeVo::getGoodsName).orElse(""))
//                .setGoodsStorePrice(optGoodsSpec.map(ShopGoodsSpec::getSpecRetailPrice).orElse(BigDecimal.ZERO))
                .setSpecId(optCart.map(ShopCartExchangeVo::getSpecId).orElse(0L))
                .setGoodsId(optCart.map(ShopCartExchangeVo::getGoodsId).orElse(0L))
                .setQuantity(optCart.map(ShopCartExchangeVo::getGoodsNum).orElse(0))
                .setDefaultImage(optCart.map(ShopCartExchangeVo::getGoodsImages).orElse(""));
        itemResult.setPpv(optGoodsSpec.map(ShopGoodsSpec::getPpv).orElse(BigDecimal.ZERO));
        itemResult.setVipPrice(optGoodsSpec.map(ShopGoodsSpec::getSpecMemberPrice).orElse(BigDecimal.ZERO));
        itemResult.setBigPpv(optGoodsSpec.map(ShopGoodsSpec::getBigPpv).orElse(BigDecimal.ZERO));
        itemResult.setBigPrice(optGoodsSpec.map(ShopGoodsSpec::getSpecBigPrice).orElse(BigDecimal.ZERO));
        ShopGoods goods = cart.getGoods();
        if(goods!=null&&goods.getPlusVipType()!=null){
            itemResult.setPlusVipType(goods.getPlusVipType());
        }else {
            itemResult.setPlusVipType(0);
        }
        return itemResult;
    }
}
