package com.framework.loippi.entity.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * 预售商品表
 * @author :ldq
 * @date:2020/4/10
 * @description:dubbo com.framework.loippi.entity.product
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shop_goods_presale")
public class ShopGoodsPresale implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** id */
	@Column(id = true, name = "id", updatable = false)
	private Long id;

	/** 订单编号 */
	@Column(name = "`goods_id`" )
	private Long goodsId;

	/**
	 * 规格id
	 */
	@Column(name = "spec_id")
	private Long specId;

	/**
	 * 规格商品编号SKU
	 */
	@Column(name = "spec_goods_serial")
	private String specGoodsSerial;
}
