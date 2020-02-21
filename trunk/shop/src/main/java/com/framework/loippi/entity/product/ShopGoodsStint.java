package com.framework.loippi.entity.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * @author :ldq
 * @date:2020/2/20
 * @description:dubbo com.framework.loippi.entity.product
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shop_goods_stint")
public class ShopGoodsStint implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/**
	 * 索引id
	 */
	@Column(id = true, name = "id", updatable = false)
	private Long id;

	/**
	 * 商品id
	 */
	@Column(name = "goods_id")
	private Long goodsId;

	/**
	 * 商品名称
	 */
	@Column(name = "goods_name")
	private String goodsName;

	/**
	 * 规格id
	 */
	@Column(name = "spec_id")
	private Long specId;

	/**
	 * 规格商品编号
	 */
	@Column(name = "spec_goods_serial")
	private String specGoodsSerial;

	/**
	 * 限购数量
	 */
	@Column(name = "stint_num")
	private Integer stintNum;


}
