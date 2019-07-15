package com.framework.loippi.pojo.cart;

import com.framework.loippi.entity.cart.ShopCart;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class  CartVo extends ShopCart {

    /**
     * 代金券金额 店铺优惠券（类型为代金券）
     */
    private BigDecimal itemVoucherPrice = new BigDecimal(0);

    /**
     * 优惠券金额
     */
    private BigDecimal itemCouponPrice = new BigDecimal(0);

    /**
     * 活动优惠金额
     */
    private BigDecimal itemPromotionPrice = new BigDecimal(0);

    /**
     * 购物车多个商品价格
     */
    private BigDecimal itemTotalPrice;

    /**
     * 商品真实购买价格
     */
    private BigDecimal actualPrice;
}
