package com.framework.loippi.entity.product;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import java.util.List;

import com.framework.loippi.result.common.goods.IdNameDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity - 组合商品，商品选择
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_GOODS_GOODS")
public class ShopGoodsGoods implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * id
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 商品id
     */
    @Column(name = "good_id")
    private Long goodId;

    /**
     * 组合商品id
     */
    @Column(name = "combine_goods_id")
    private Long combineGoodsId;

    /**
     * 库存
     */
    @Column(name = "stock")
    private Integer stock;

    /**
     * 规格值
     */
    @Column(name = "goods_spec")
    private String goodsSpec;

    /**
     * 参与组合商品数量
     */
    @Column(name = "join_num")
    private Integer joinNum;

    //零售价
    private java.math.BigDecimal goodsRetailPrice;
    //会员价格
    private java.math.BigDecimal goodsMemberPrice;
    //大单价
    private java.math.BigDecimal goodsBigPrice;
    // ppv
    private Integer ppv;
    //大单pv
    private Integer bigPpv;

    List<IdNameDto> shopGoodsSpecList;

    private String goodsName;

    private List<Long> combineGoodsIds;

}
