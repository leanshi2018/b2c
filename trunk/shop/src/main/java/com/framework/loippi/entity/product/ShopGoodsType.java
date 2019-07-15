package com.framework.loippi.entity.product;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import com.framework.loippi.vo.goods.SpecVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Entity - 商品类型表
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_GOODS_TYPE")
public class ShopGoodsType implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 类型id
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 类型名称
     */
    @Column(name = "type_name")
    private String typeName;

    /**
     * 排序
     */
    @Column(name = "type_sort")
    private Integer typeSort;

    /**
     * 父id
     */
    @Column(name = "st_parent_id")
    private Long stParentId;

    /**
     * 层级path
     */
    @Column(name = "st_idpath")
    private String stIdpath;

    /**
     * 费用比例
     */
    @Column(name = "expen_scale")
    private BigDecimal expenScale;

    /**
     * 是否开启自定义模版
     */
    @Column(name = "open_custom_spec")
    private Integer openCustomSpec;

    /**
     * 是否开启自定义属性模版
     */
    @Column(name = "open_custom_attr")
    private Integer openCustomAttr;

    /**
     * 商品品牌List
     */
    private List<ShopGoodsTypeBrand> brandList;

    /**
     * 商品规格List
     */
    private List<ShopGoodsTypeSpec> specList;
    /**
     * 商品规格值
     */
    private List<SpecVo> specVos;

    private List<ShopGoodsBrand> brandListByType;
    /**
     * 描述
     */
    @Column(name = "description")
    private String description;


}
