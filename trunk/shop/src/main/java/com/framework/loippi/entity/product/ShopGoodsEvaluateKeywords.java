package com.framework.loippi.entity.product;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity - 评价关键字表
 * 
 * @author dzm
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_GOODS_EVALUATE_KEYWORDS")
public class ShopGoodsEvaluateKeywords implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/**  */
	@Column(id = true, name = "id", updatable = false)
	private Long id;
	
	/** 关键字 */
	@Column(name = "keywords" )
	private String keywords;

	private Integer countNum;
	
}
