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
	 * 零售价（普通价）
	 */
	private BigDecimal goodsRetailPrice;
	/**
	 * 会员价格
	 */
	private BigDecimal goodsMemberPrice;

}
