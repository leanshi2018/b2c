package com.framework.loippi.vo.store;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * @author :ldq
 * @date:2020/6/11
 * @description:dubbo com.framework.loippi.vo.store
 */
@Data
@ToString
public class MentionWareGoodsVo {

	/** 仓库代码：yyyyxxxx */
	private String wareCode;

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
	 * 零售价（普通价）
	 */
	private BigDecimal goodsRetailPrice;

	/**
	 * 会员价（会员价）
	 */
	private BigDecimal goodsMemberPrice;
	/**
	 * 大单价格
	 */
	private BigDecimal goodsBigPrice;

	/**
	 * ppv（mi值）
	 */
	private BigDecimal ppv;
	/**
	 * 大单Ppv
	 */
	private BigDecimal bigPpv;
	/**
	 * 成本价
	 */
	private BigDecimal costPrice;

	/**
	 * 销量
	 SELECT sum(sog.goods_num) FROM
	 (SELECT so.id id,soa.mention_id mention_id FROM shop_order as so LEFT JOIN shop_order_address as soa ON so.address_id = soa.id
	 WHERE so.payment_state=1 AND so.order_state <> 0 AND ifnull(so.refund_state,0) = 0 AND so.logistic_type =2 ) vsfs
	 LEFT JOIN shop_order_goods as sog
	 on vsfs.id = sog.order_id
	 WHERE vsfs.mention_id = -5 AND sog.spec_id = '6592323279883603968'
	 group by vsfs.mention_id,sog.spec_id
	 */
	private Integer sales;

	/** 库存 */
	private Integer inventory;

	/** 公司库存 */
	private Integer inventoryGc;

	/** 单品实际库存 */
	private Integer productInventory;

	/** 商品类型1-普通2-换购3-组合 */
	private Integer goodsType;

	/** 是否为plus vip商品 0：不是 1：是 */
	private Integer plusVipType;
}
