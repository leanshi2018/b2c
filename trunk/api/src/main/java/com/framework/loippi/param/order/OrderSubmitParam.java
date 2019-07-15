package com.framework.loippi.param.order;

import java.math.BigDecimal;
import java.util.Map;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 功能： 提交订单参数
 * 类名：OrderSubmitParam
 * 日期：2017/11/30  13:39
 * 作者：czl
 * 详细说明：
 * 修改备注:
 */
@Data
public class OrderSubmitParam {

    /**
     * 订单留言
     */
    private String orderMessages;

    /**
     * 购物车id
     */
    @NotNull
    private String cartIds;

    /**
     * 收货地址id
     */
    private Long addressId;

    /**
     * 订单类型id
     */
    @NotNull
    private Long shopOrderTypeId;

    /**
     * 是否购物积分支付
     */
    @Max(1)
    @Min(0)
    @NotNull
    private Integer isPp;

    /**
     * 1快递 2自提
     */
    @NotNull
    private Integer logisticType;
    /**
     * 1在线支付 2货到付款
     */
    @NotNull
    private Integer paymentType;
    /**
     * 提货人姓名
     */
    String userName;
    /**
     * 提货人电话
     */
    String userPhone;

    /**
     * 团购活动id
     */
    private Long groupBuyActivityId;

    /**
     * 开团订单id
     */
    private Long groupOrderId;  

}
