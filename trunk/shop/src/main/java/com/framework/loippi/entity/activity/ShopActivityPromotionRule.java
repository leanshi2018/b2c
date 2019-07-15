package com.framework.loippi.entity.activity;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 促销活动规则表Entity
 *
 * @author kwg
 * @version 2016-09-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_ACTIVITY_PROMOTION_RULE")
public class ShopActivityPromotionRule implements GenericEntity {

    private static final long serialVersionUID = 1L;


    /**
     * 促销活动规则id
     */
    private Long id;

    /**
     * 促销活动规则id
     */
    @Column(name = "activity_id")
    private Long activityId;

    /**
     * 限制类型--1 购买金额 、2购买数量   0无限制
     */
    @Column(name = "LIMIT_TYPE")
    private Integer limitType;


    /**
     * 限制条件 金额/数量 等
     */
    @Column(name = "LIMIT_WHERE")
    private String limitWhere;


    /**
     * 优先级
     */
    @Column(name = "PRIORITY")
    private Integer priority;


    /**
     * 优惠资源
     */
    @Column(name = "COUPON_SOURCE")
    private String couponSource;

    /**
     * 活动描述
     */
    @Column(name = "description")
    private String description;

    /**
     * 规则名称
     */
    @Column(name = "rule_title")
    private String ruleTitle;

    /**
     * //团购50/限时抢购60//活动70
     */
    @Column(name = "rule_type")
    private Integer ruleType;

    @Column(name = "sort")
    private Integer sort;

    private Long isActivityIdNull;

}