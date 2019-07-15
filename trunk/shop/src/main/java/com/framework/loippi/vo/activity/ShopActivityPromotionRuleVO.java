package com.framework.loippi.vo.activity;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import lombok.Data;
import lombok.ToString;

/**
 * 促销活动规则表Entity
 *
 * @author kwg
 * @version 2016-09-01
 */
@Data
@ToString
public class ShopActivityPromotionRuleVO implements GenericEntity {
	
	//表里没有 冗余字段 begin
	/**
	 * 规格值json
	 */
	private String specGoodsSpec;

	/**
	 * 商品名称
	 */
	private String sourceName;

	/**
	 * 图片
	 */
	private String sourceImage;
	//表里没有 冗余字段 end
	
	/**
	 * 规格id
	 */
	private String goodsSpecId;

}