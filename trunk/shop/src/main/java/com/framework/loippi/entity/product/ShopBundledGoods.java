package com.framework.loippi.entity.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * @author :ldq
 * @date:2021/1/15
 * @description:dubbo com.framework.loippi.entity.product
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shop_bundled_goods")
public class ShopBundledGoods implements GenericEntity {

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
	 * 规格id
	 */
	@Column(name = "spec_id")
	private Long specId;

	/**
	 * 附赠商品id
	 */
	@Column(name = "b_goods_id")
	private Long bGoodsId;

	/**
	 * 附赠规格id
	 */
	@Column(name = "b_spec_id")
	private Long bSpecId;

	/**
	 * 附赠商品数量
	 */
	@Column(name = "b_num")
	private Integer bNum;
}
