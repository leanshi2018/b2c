package com.framework.loippi.param.cart;

import com.framework.loippi.enus.ActivityTypeEnus;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Param - 加入购物车
 *
 * @author Loippi team
 * @version 2.0
 * @description 加入购物车
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartAddParam {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 商品id
     */
    @NotNull
    private Long goodsId;
    /**
     * 购买数量
     */
    @Min(1)
    @NotNull
    private Integer count;

    /**
     * 规格
     */
    @NotNull
    private Long specId;

    /**
     * 活动id
     */
    private Long activityId;

    //活动规格id
    private Long activitySkuId;

    //活动商品id
    private Long activityGoodsId;

    /**
     * 活动类型
     *
     * @see ActivityTypeEnus
     */
    private Integer activityType;

}
