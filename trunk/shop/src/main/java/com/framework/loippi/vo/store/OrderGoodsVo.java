package com.framework.loippi.vo.store;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author :ldq
 * @date:2020/6/24
 * @description:dubbo com.framework.loippi.vo.store
 */
@Data
@ToString
public class OrderGoodsVo {

	/** 商品id */
	private Long goodId;

	/** 商品名称 */
	private String goodsName;

	/** 商品默认封面图片*/
	private String goodsImage;

	/** 规格名称 */
	private String specName;

	/** 商品规格 */
	private String goodsSpec;

	/** 入库商品数量 */
	private Long stockInto;
	/**
	 * ppv
	 */
	private BigDecimal ppv;
	/**
	 * 大单Ppv
	 */
	private BigDecimal bigPpv;
	/**
	 * 零售价（普通价）
	 */
	private BigDecimal goodsRetailPrice;
	/**
	 * 会员价格
	 */
	private BigDecimal goodsMemberPrice;
	/**
	 * 大单价格
	 */
	private BigDecimal goodsBigPrice;
	/**
	 * 成本价
	 */
	private BigDecimal costPrice;


	/** 欠货数量 */
	private Integer oweInventory;

	/** 补货数量 */
	private Integer comeInventory;

	/** 商品类型1-普通2-换购3-组合 */
	private Integer goodsType;

	/** 是否为plus vip商品 0：不是 1：是 */
	private Integer plusVipType;
}
