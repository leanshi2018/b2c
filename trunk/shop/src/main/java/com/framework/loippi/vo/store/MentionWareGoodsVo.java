package com.framework.loippi.vo.store;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

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
	 * ppv（mi值）
	 */
	private BigDecimal ppv;

	/**
	 * 销量
	 SELECT vsfs.mention_id,sog.goods_type,sog.goods_id,sgs.spec_goods_serial,sog.goods_name,sog.spec_info,sum(sog.goods_num) as sum_goods_num FROM
	 (select * from b2cshop.v_so_from_stores where payment_time>='2020-06-06 00:00:00' and payment_time<'2020-06-11 00:00:00' and mention_id='-5') as vsfs
	 inner join shop_order_goods as sog
	 on vsfs.id = sog.order_id
	 inner join shop_goods_spec as sgs
	 on sog.goods_id=sgs.goods_id
	 group by vsfs.mention_id,sog.goods_type,sog.goods_id,sog.goods_name,sog.spec_info
	 */
	private Integer sales;

	/** 库存 */
	private Integer inventory;

	/** 单品实际库存 */
	private Integer productInventory;

}
