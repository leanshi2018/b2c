package com.framework.loippi.vo.order;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

/**
 * 发货单excel超类
 */
@Data
public class ShippedOrderExcelVo {

    /**
     * 发货单号
     */
    private String orderSn;

    /**
     * 会员号
     */
    private String buyerName;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 售价
     */
    private BigDecimal goodsPrice;

    /**
     * 数量
     */
    private Integer goodsCount;

    /**
     * 实付款
     */
    private BigDecimal orderAmount;

    /**
     * 收件人
     */
    private String receiverName;


    /**
     * 手机号码
     */
    private String receiverPhone;


    /**
     * 收货人地址
     */
    private String reciverAddr;

    /**
     * 物流公司
     */
    private String shippingExpress;

    /**
     * 物流费用
     */
    private BigDecimal freight;


    /**
     * 物流单号
     */
    private String shippingCode;


    /**
     * 创建时间
     */
    private Date createTime;
}
