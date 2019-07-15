package com.framework.loippi.entity.cart;

import com.framework.loippi.enus.ActivityRuleTypeEnus;
import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import java.math.BigDecimal;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity - 购物车数据表
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_CART")
public class ShopCart implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 购物车id
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 会员id
     */
    @Column(name = "member_id")
    private Long memberId;

    /**
     * 商品id
     */
    @Column(name = "goods_id")
    private Long goodsId;

    /**
     * 商品名称
     */
    @Column(name = "goods_name")
    private String goodsName;

    /**
     * 规格id
     */
    @Column(name = "spec_id")
    private Long specId;

    /**
     * 规格内容
     */
    @Column(name = "spec_info")
    private String specInfo;

    /**
     * 零售价
     */
    @Column(name = "goods_retail_price")
    private java.math.BigDecimal goodsRetailPrice;

    /**
     * 会员价格
     */
    @Column(name = "goods_member_price")
    private java.math.BigDecimal goodsMemberPrice;

    /**
     * 大单价
     */
    @Column(name = "goods_big_price")
    private java.math.BigDecimal goodsBigPrice;

    /**
     * ppv
     */
    @Column(name = "ppv")
    private BigDecimal ppv;

    /**
     * 大单pv
     */
    @Column(name = "big_ppv")
    private BigDecimal bigPpv;

    /**
     * 购买商品数量
     */
    @Column(name = "goods_num")
    private Integer goodsNum;

    /**
     * 商品图片
     */
    @Column(name = "goods_images")
    private String goodsImages;

    /**
     * 商品所在一级分类的id
     */
    @Column(name = "first_gc_id")
    private Long firstGcId;

    /**
     * 商品类型
     */
    @Column(name = "goods_state")
    private Integer goodsState;

    /**
     * 活动id
     */
    @Column(name = "activity_id")
    private Long activityId;

    /**
     * 活动商品id
     */
    @Column(name = "activity_goods_id")
    private Long activityGoodsId;

    /**
     * 活动规格id
     */
    @Column(name = "activity_spec_id")
    private Long activitySpecId;

    /**
     * 参考CommonConstants
     */
    @Column(name = "activity_type")
    private Integer activityType;
    /**
     * 品牌id
     */
    @Column(name = "brand_id")
    private Long brandId;

    /**
     * 品牌名称
     */
    @Column(name = "brand_name")
    private String brandName;

    /**
     * 商品类型id
     */
    @Column(name = "goods_type")
    private Integer goodsType;

    /**
     * 活动规则
     *
     * @see ActivityRuleTypeEnus
     */
    private Integer ruleType;
    /**
     * 商品重量
     */
    @Column(name = "weight")
    private Double weight;

    private List<Long> specIds;

}
