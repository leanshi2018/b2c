package com.framework.loippi.param.order;

import com.framework.loippi.enus.ActivityTypeEnus;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 功能： 立即购买提交订单参数
 * 类名：OrderSubmitParam
 * 日期：2017/11/30  13:39
 * 作者：czl
 * 详细说明：
 * 修改备注:
 */
@Data
public class OrderImmediatelySubmitParam {

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

    /**
     * 订单留言
     */
    private String orderMessages;

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
    private String userName;
    /**
     * 提货人电话
     */
    private String userPhone;
    /**
     * 赠品id
     */
    private Long giftId;

    /**
     * 赠品数量
     */
    private Integer giftNum;

    /**
     * 优惠券id
     */
    private Long couponId;

    /**
     * 平台来源 小程序传weixinAppletsPaymentPlugin
     */
    private String platform;
    /**
     * 是否plus vip订单
     */
    private Integer plusOrderFlag;
    /**
     * 是否分单 0：不分单 1：分单
     */
    @Min(0)
    @Max(1)
    @NotNull
    private Integer splitOrderFlag;
    /**
     * 分单会员拼接字符串
     */
    private String splitCodes;
}
