package com.framework.loippi.entity.gift;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * @author :ldq
 * @date:2020/12/1
 * @description:dubbo com.framework.loippi.entity.gift
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shop_gift_goods")
public class ShopGiftGoods implements GenericEntity {
	private static final long serialVersionUID = 5081846432919091193L;
	/**
	 * 主键id索引
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;
	/**
	 * 赠品规则表Id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long giftId;
	/**
	 * 商品id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long goodsId;
	/**
	 * 商品名称
	 */
	private String goodsName;
	/**
	 * 规格id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long specId;
	/**
	 * 第几个规则（1,2,3）仅有3个规则
	 */
	private Integer wRule;

	private String specInfo;//规格值
}
