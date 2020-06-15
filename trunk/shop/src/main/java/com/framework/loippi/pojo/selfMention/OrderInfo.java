package com.framework.loippi.pojo.selfMention;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 自提订单信息
 */
@Data
public class OrderInfo {
    /**
     * 订单编号
     */
    private Long orderId;

    /**
     * 支付时间
     */
    private Date paymentTime;

    /**
     * 购买人编号
     */
    private Long buyerId;
    /**
     * 订单编号
     */
    private String orderSn;

    /**
     * 购买人手机号
     */
    private String buyerPhone;

    /**
     * 购买人姓名
     */
    private String buyerName;

    /**
     * 订单类型
     */
    private Integer orderType;

    /**
     * 创建周期
     */
    private String creationPeriod;
    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 现金支付金额
     */
    private BigDecimal orderAmount;

    /**
     * 使用积分金额
     */
    private BigDecimal usePointNum;

    /**
     * 总的优惠金额
     */
    private BigDecimal discount;

    /**
     * 优惠券优惠金额
     */
    private BigDecimal couponDiscount;
    /**
     * 等级优惠金额
     */
    private BigDecimal rankDiscount;
    /**
     * 商品总金额
     */
    private BigDecimal goodsAmount;
    /**
     * 运费
     */
    private BigDecimal shippingFee;

    /**
     * 运费优惠价格
     */
    private BigDecimal shippingPreferentialFee;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退款积分
     */
    private BigDecimal refundPoint;

    /**
     * 订单状态
     */
    private Integer orderState;
}
