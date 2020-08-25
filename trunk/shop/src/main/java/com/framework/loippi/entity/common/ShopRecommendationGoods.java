package com.framework.loippi.entity.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * 推荐页商品表
 * @author :ldq
 * @date:2020/8/21
 * @description:dubbo com.framework.loippi.entity.common
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shop_recommendation_goods")
public class ShopRecommendationGoods implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/**
	 * id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;

	/**
	 * 推荐页id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long rId;

	/**
	 * 商品id
	 */
	@JsonSerialize(using = ToStringSerializer.class)
	private Long goodsId;
}
