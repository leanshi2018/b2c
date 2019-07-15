package com.framework.loippi.pojo.order;

import com.framework.loippi.entity.cart.ShopCart;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 购物车订单商品转换过渡实体
 *
 * @author liukai
 */
@Data
public class CartOrderVo extends ShopCart {

    /**
     * 商品实际成交价- 不含运费税费
     */
    private BigDecimal goodsPrice;

    /**
     * 现金支付-支付宝 微信等
     */
    private BigDecimal goodsCashAmount;

    /**
     * 优惠券
     */
    private BigDecimal goodsCouponPrice;

    /**
     * 代金券
     */
    private BigDecimal goodsVoucherPrice;

    /**
     * 打赏积分抵扣
     */
    private BigDecimal goodsRewardPointPrice;

}
