package com.framework.loippi.entity.product;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Entity - 商品规格表
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_GOODS_SPEC")
public class ShopGoodsSpec implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 商品规格索引id
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 商品id
     */
    @Column(name = "goods_id")
    private Long goodsId;

    /**
     * 规格名称
     */
    @Column(name = "spec_name")
    private String specName;

    /**
     * 大单价
     */
    @Column(name = "spec_big_price")
    private BigDecimal specBigPrice;

    /**
     * 零售价
     */
    @Column(name = "spec_retail_price")
    private BigDecimal specRetailPrice;

    /**
     * 零售利润
     */
    @Column(name = "spec_retail_profit")
    private BigDecimal specRetailProfit;

    /**
     * 会员价
     */
    @Column(name = "spec_member_price")
    private BigDecimal specMemberPrice;

    /**
     * 规格商品库存
     */
    @Column(name = "spec_goods_storage")
    private Integer specGoodsStorage;

    /**
     * 售出数量
     */
    @Column(name = "spec_salenum")
    private Integer specSalenum;

    /**
     * 规格商品颜色
     */
    @Column(name = "spec_goods_color")
    private String specGoodsColor;

    /**
     * 规格商品编号SKU
     */
    @Column(name = "spec_goods_serial")
    private String specGoodsSerial;

    /**
     * 商品规格序列化
     */
    @Column(name = "spec_goods_spec")
    private String specGoodsSpec;

    /**
     * 是否开启规格,1:开启，0:关闭 3修改后不用的
     */
    @Column(name = "spec_isopen")
    private Integer specIsopen;

    /**
     * 条形码
     */
    @Column(name = "spec_bar_code")
    private String specBarCode;

    /**
     * 图片
     */
    @Column(name = "spec_pic")
    private String specPic;
    /**
     * 商品pv值
     */
    @Column(name = "ppv")
    private BigDecimal ppv;
    /**
     * 大单pv值
     */
    @Column(name = "big_ppv")
    private BigDecimal bigPpv;
    /**
     * 重量
     */
    @Column(name = "weight")
    private Double weight;
    /**
     * 保质期
     */
    @Column(name = "shelf_life")
    private Integer shelfLife;

    /*********************添加*********************/
    /**
     * 所有SpecValueId用逗号隔开
     */
    private String specValueIDS;

    /**
     * 当前商品的 规格名称 对应的规格值
     */
    private Map<String, String> sepcMap;
    private Map<String, String> specValueMap;
    //查询条件
    private Long combinationIndex;
    private String keyWord;

    private Long[] shopGoodsIds;
    private Map<String, String> imageMap;

}
