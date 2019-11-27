package com.framework.loippi.pojo.order;

import com.google.common.collect.Lists;
import java.math.BigDecimal;
import java.util.List;
import javax.naming.ldap.PagedResultsControl;
import lombok.Data;
import lombok.ToString;

/**
 * 生成订单使用的订单超类
 *
 * @author liukai
 */
@Data
@ToString
public class OrderVo {

    /**
     * 购物车集合
     */
    private List<CartOrderVo> cartOrderVoList = Lists.newArrayList();

    /**
     * 商品总数量
     */
    private int goodsNum;

    /**
     * 商品总价格
     */
    private BigDecimal goodsAmount;

    /**
     * 品牌id
     */
    private Long brandId;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 订单总价(应付金额)
     */
    private BigDecimal orderAmount;
    /**
     * 订单pv值
     */
    private BigDecimal ppv;
    /**
     * 商品优惠价格
     */
    private BigDecimal couponAmount;
    /**
     *运费
     */
    public BigDecimal freightAmount;
    /**
     *优惠运费
     */
    public BigDecimal preferentialFreightAmount;
    /**
     *订单使用优惠券金额
     */
    public BigDecimal useCouponAmount;
//    /**
//     * 代金券金额
//     */
//    private BigDecimal voucherPrice;

//    /**
//     * 优惠券金额
//     */
//    private BigDecimal couponPrice;

//    /**
//     * 优惠券id
//     */
//    private Long couponId;
//
//    /**
//     * 代金券id
//     */
//    private Long voucherId;

    /**
     * 打赏积分抵扣金额
     */
    private BigDecimal rewardPointPrice;

//    /**
//     * 积分支付金额
//     */
//    private BigDecimal pointPrice;

//
//    /**
//     * 优惠券id
//     */
//    private Long couponId;
//
//    /**
//     * 优惠券金额
//     */
//    private BigDecimal couponPrice;

//    /**
//     * 促销金额
//     */
//    private BigDecimal promoPrice;

//    /**
//     * 优惠总金额
//     */
//    private BigDecimal discount;

//
//    /**
//     * 优惠类型
//     */
//    private String promotType;
}