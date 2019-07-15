package com.framework.loippi.entity.product;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity - 浏览记录表
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_GOODS_BROWSE")
public class ShopGoodsBrowse implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 浏览记录id
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 会员id
     */
    @Column(name = "browse_member_id")
    private Long browseMemberId;

    /**
     * 商品Id
     */
    @Column(name = "browse_goods_id")
    private Long browseGoodsId;

     /**
     * 活动Id
     */
    @Column(name = "browse_activity_id")
    private Long browseActivityId;


    /**
     * 商品名称
     */
    @Column(name = "browse_goods_name")
    private String browseGoodsName;

    /**
     * 商品图片
     */
    @Column(name = "browse_goods_image")
    private String browseGoodsImage;

    /**
     * 商品价格
     */
    @Column(name = "browse_goods_price")
    private BigDecimal browseGoodsPrice;

    /**
     * 商品vip价格
     */
    @Column(name = "browse_goods_vip_price")
    private BigDecimal browseGoodsVipPrice;

    /**
     * 商品分类id
     */
    @Column(name = "browse_gc_id")
    private Long browseGcId;

    /**
     * 分类名称
     */
    @Column(name = "browse_gc_name")
    private String browseGcName;

    /**
     * 商品规格Id
     */
    @Column(name = "browse_spec_id")
    private Long browseSpecId;

    /**
     * 品牌id
     */
    @Column(name = "browse_brand_id")
    private Long browseBrandId;

    /**
     * 品牌名称
     */
    @Column(name = "browse_brand_name")
    private String browseBrandName;

    /**
     * 浏览类型 默认0 商品记录0  店铺记录1
     */
    @Column(name = "browse_state")
    private Integer browseState;

    /**
     * 浏览次数
     */
    @Column(name = "browse_num")
    private Integer browseNum;

    /**
     * 浏览时间
     */
    @Column(name = "create_time")
    private Date createTime;


    private String days;

}
