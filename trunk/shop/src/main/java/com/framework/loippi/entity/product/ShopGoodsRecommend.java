package com.framework.loippi.entity.product;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity - 商品推荐
 *
 * @author longbh
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_GOODS_RECOMMEND")
public class ShopGoodsRecommend implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * id
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 商品id
     */
    @Column(name = "goods_id")
    private Long goodsId;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private java.util.Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private java.util.Date updateTime;

    /**
     * 推荐图片，逗号分隔
     */
    @Column(name = "recommend_image")
    private String recommendImage;

    /**
     * 开始时间
     */
    @Column(name = "start_time")
    private java.util.Date startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time")
    private java.util.Date endTime;

    /**
     * 排序
     */
    @Column(name = "sort")
    private Integer sort;

    /**
     * 1-正常2-警用
     */
    @Column(name = "status")
    private Integer status;

    private String goodsName;
    private String goodsImage;
    private Integer goodsCollect;
    private Integer goodsType;

    List<String> imageList;
    private ShopGoods shopGoods;
    private List<Long> noBrandIds;

}
