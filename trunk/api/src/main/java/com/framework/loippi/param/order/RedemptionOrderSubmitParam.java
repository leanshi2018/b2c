package com.framework.loippi.param.order;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 换购商品购物车提交订单
 */
@Data
public class RedemptionOrderSubmitParam {

    /**
     * 购物车id
     */
    @NotNull
    private String cartIds;

    /**
     * 收货地址id
     */
    @NotNull
    private Long addressId;

}
