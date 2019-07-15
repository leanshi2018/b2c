package com.framework.loippi.entity.activity;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Entity - 活动规格
 *
 * @author longbh
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_ACTIVITY_GOODS_SPEC")
public class ShopActivityGoodsSpec implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    //活动商品规格
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    ///商品id
    @Column(name = "goods_id")
    private Long goodsId;

    //活动id
    @Column(name = "activity_id")
    private Long activityId;

    //活动商品id
    @Column(name = "activity_goods_id")
    private Long activityGoodsId;

    //规格id
    @Column(name = "spec_id")
    private Long specId;

    //活动价
    @Column(name = "activity_price")
    private BigDecimal activityPrice;

    //活动库存
    @Column(name = "activity_stock")
    private Integer activityStock;

    //创建时间
    @Column(name = "create_time")
    private java.util.Date createTime;

    private Map<String, String> sepcMap;
    private Map<String, String> specValueMap;
    private List<Long> specIds;
    private List<Long> ids;

}
