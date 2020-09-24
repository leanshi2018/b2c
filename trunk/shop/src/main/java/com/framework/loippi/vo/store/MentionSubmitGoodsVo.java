package com.framework.loippi.vo.store;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author :ldq
 * @date:2020/9/23
 * @description:dubbo com.framework.loippi.vo.store
 */
@Data
@ToString
public class MentionSubmitGoodsVo {
	/**
	 * 商品名称
	 */
	private String goodsName;

	/**
	 * 商品默认封面图片
	 */
	private String goodsImage;

	/**
	 * 规格Id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long specId;

	/**
	 * 商品规格
	 */
	private String specGoodsSpec;


	/**
	 * ppv（mi值）
	 */
	private BigDecimal ppv;

	/**
	 * 成本价
	 */
	private BigDecimal costPrice;


	/** 欠货数量 */
	private Integer oweInventory;

	/** 补货数量 */
	private Integer comeInventory;

}
