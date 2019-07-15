package com.framework.loippi.entity.product;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity - 商品与规格对应表
 * 
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_GOODS_SPEC_INDEX")
public class ShopGoodsSpecIndex implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/**  */
	@Column(id = true, name = "id", updatable = false)
	private Long id;
	
	/** 商品id */
	@Column(name = "goods_id" )
	private Long goodsId;
	
	/** 商品分类id */
	@Column(name = "gc_id" )
	private Long gcId;
	
	/** 类型id */
	@Column(name = "type_id" )
	private Long typeId;
	
	/** 规格id */
	@Column(name = "sp_id" )
	private Long spId;
	
	/** 规格值id */
	@Column(name = "sp_value_id" )
	private Long spValueId;
	
	/** 规格值名称 */
	@Column(name = "sp_value_name" )
	private String spValueName;
	
}
