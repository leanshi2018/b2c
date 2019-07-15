package com.framework.loippi.vo.cart;

import com.framework.loippi.entity.cart.ShopCart;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import lombok.Data;

/**
 * 购物车实体类扩展
 *
 * @author zijing
 * @version 2.0
 */
@Data
public class ShopCartVo extends ShopCart {

    /**
     * 购物车商品详
     */
    private ShopGoods goods;

    /**
     * 商品规格详情
     */
    private ShopGoodsSpec goodsSpec;

//    /**
//     * 活动状态  10  未开始  20 活动中 30已结束
//     */
//    private Integer activityStatus;
//
//    /**
//     * 审核状态
//     * 0 禁用 1开启
//     */
//    private Integer auditStatus;
//
//    /**
//     * 商品活动价格
//     */
//    private BigDecimal goodsPrice;
//    /**
//     * 优惠类型
//     */
//    private Integer promotionType;
//
//    /**
//     * 最大购买数量
//     */
//    private Integer maxBuyNum;
//
//    /**
//     * 最少购买数
//     */
//    private Integer minBuyNum;
//
//
//
//    /**
//     * 税费
//     */
//    private BigDecimal tax;
//
//    /**
//     * 红包抵扣金额
//     */
//    private BigDecimal hBDeduction;
//
//    /**
//     * 余额抵扣金额
//     */
//    private BigDecimal payByBalanceRate;
//
//    /**
//     * 是否包邮 0否 1是
//     */
//    private Integer isFreeShipping;
//
//    /**
//     * 是否免税 0否 1是
//     */
//    private Integer isFreeTax;
//
//    /**
//     * 该商品列入满选价格计算情况
//     *
//     * 参与了满选计算：1   没参与满选计算：0 或 null
//     */
//    private Integer isManXuan;
//
//    /**
//     * 参与满选的商品数
//     */
//    private Integer maxXuanNum;
//
//    /**
//     * 参与满选的商品价格  所有参与满选的商品总金额
//     */
//    private BigDecimal maxXuanPrice;
//
//    /**
//     * 购物车id
//     */
//    private Long couponId;
}
