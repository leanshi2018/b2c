package com.framework.loippi.entity.common;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Entity - 商品规格模板表
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_COMMON_SPEC")
public class ShopCommonSpec implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 规格id
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 规格名称
     */
    @Column(name = "sp_name")
    private String spName;

    /**
     * 0:text; 1:image
     */
    @Column(name = "sp_format")
    private Integer spFormat;

    /**
     * 规格值列
     */
    @Column(name = "sp_value")
    private String spValue;

    /**
     * 排序
     */
    @Column(name = "sp_sort")
    private Integer spSort;

    /**
     * 分类ID
     */
    @Column(name = "class_id")
    private Long classId;

    /**
     * 店铺Id
     */
    @Column(name = "store_id")
    private Long storeId;

    /**
     * 是否为模版
     */
    @Column(name = "is_template")
    private Integer isTemplate;

    /**
     * 分组名称
     */
    @Column(name = "spec_group_name")
    private String specGroupName;

    /**
     * 商品ID
     */
    @Column(name = "goods_id")
    private String goodsId;


     // 规格名称
    private String spNameLike;


    private List<Long> ids;

}
