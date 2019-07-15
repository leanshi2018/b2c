package com.framework.loippi.vo.activity;

import com.framework.loippi.entity.activity.ShopActivity;
import com.framework.loippi.entity.activity.ShopActivityPromotionRule;
import lombok.Data;

/**
 * 功能： 优惠券
 * 类名：ShopCouponVo
 * 日期：2017/11/24  9:31
 * 作者：czl
 * 详细说明：
 * 修改备注:
 */
@Data
public class ShopStoreCouponVo extends ShopActivityPromotionRule {

    /**
     * 优惠券基本信息
     */
    private ShopActivity shopActivity;

    /**
     * 判断优惠是否已经领取
     * //0 未领取 1已领取
     */
    private Integer hadGet;

    /**
     * 关联活动id（绑定其他活动商品）
     */
    private Long associateActivityId;
}

