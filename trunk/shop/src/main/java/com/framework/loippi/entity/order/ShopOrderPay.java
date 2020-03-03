package com.framework.loippi.entity.order;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import java.math.BigDecimal;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity - 订单支付表
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_ORDER_PAY")
public class ShopOrderPay implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**  */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 支付单号
     */
    @Column(name = "pay_sn")
    private String paySn;

    /**
     * 买家ID
     */
    @Column(name = "buyer_id")
    private Long buyerId;

    /**
     * 0默认未支付1已支付(只有第三方支付接口通知到时才会更改此状态)
     */
    @Column(name = "api_pay_state")
    private String apiPayState;
    /**
     * 1在线支付 2货到付款
     */
    private Integer paymentType;
    /*********************添加*********************/
    //订单总额
    private BigDecimal orderTotalPrice;

    //支付金额
    private BigDecimal payAmount;

    //用户余额
    private BigDecimal balance;

    //订单创建时间
    private Date orderCreateTime;

    //单个订单
    private Long orderId;

    //单个订单号
    private String orderSn;

    //订单是否使用过积分
    private Boolean usePointFlag;
}

