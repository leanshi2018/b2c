package com.framework.loippi.vo.activity;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 活动对象关联表Entity
 *
 * @author kwg
 * @version 2016-09-01
 */
@Data
@ToString
public class ShopActivityObjectVO implements GenericEntity {
	
	private static final long serialVersionUID = 1L;


	/**
	 * 活动图片
	 */
	private String id;


	/**
	 * 活动id
	 */
	private String activityId;


	/**
	 * 对象类型
	 */
	private Integer objectType;


	/**
	 * 对象id
	 */
	private String objectId;

	/**
	 * 商品规格价格
	 */
	private BigDecimal  specGoodsPrice;

	/**
	 * 活动商品价格
	 */
	private BigDecimal price;

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
	 *  参与的活动的商品库存数量
	 */
	private Integer stockNumber;

	/**
	 * 每个会员限购数量
	 */
	private Integer menNumber;

	/**
	 * 已销售人数
	 */
	private  Integer alreadyBuyNumber;

	/**
	 * 商品库存
	 */
	private Integer  specGoodsStorage;

	/**
	 *品牌名称
	 */
	private String  brandName ;

	/**
	 * 已销售商品数
	 */
	private Integer saleNumber;

	/**
	 * 规格主图
	 */
	private String  mainPicture;

	/**
	 * 规格列表图
	 */
	private String listPicture;

	/**
	 * 预销售的商品数量
	 */
	private Integer factSaleNumber;



}