package com.framework.loippi.entity.common;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity - 商品规格值表
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_GOODS_SPEC_VALUE")
public class ShopCommonSpecValue implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 规格值id
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 规格值名称
     */
    @Column(name = "sp_value_name")
    private String spValueName;

    /**
     * 所属规格id
     */
    @Column(name = "sp_id")
    private Long spId;

    /**
     * 规格图片
     */
    @Column(name = "sp_value_image")
    private String spValueImage;

    /**
     * 排序
     */
    @Column(name = "sp_value_sort")
    private Integer spValueSort;

    /**
     * 只用于页面传值：标识编译的时候是否移除了某个规格值
     */
    private String isDelete;

}
