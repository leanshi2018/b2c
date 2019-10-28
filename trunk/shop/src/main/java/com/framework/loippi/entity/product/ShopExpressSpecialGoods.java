package com.framework.loippi.entity.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * 特殊快递渠道商品表
 * @author :ldq
 * @date:2019/10/25
 * @description:dubbo com.framework.loippi.entity.product
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shop_express_special_goods")
public class ShopExpressSpecialGoods implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/**
	 * 主键id索引
	 */
	private Long id;
	/**
	 * 商品规格索引id
	 */
	private Long goodsSpecId;
	/**
	 * 商品id
	 */
	private Long goodsId;
	/**
	 * 规格商品编号
	 */
	private String specGoodsSerial;
	/**
	 * 快递公司id
	 */
	private Long expressId;
	/**
	 * 层级  1：最低级
	 */
	private Integer expLevel;
	/**
	 * 状态 0有效  1无效
	 */
	private Integer eState;
	/**
	 * 创建人
	 * */
	private String creationBy;

	/**
	 * 创建时间
	 * */
	private Date creationTime;

	/**
	 * 更新人
	 * */
	private String updateBy;

	/**
	 * 更新时间
	 * */
	private Date updateTime;

	/*
	  `id` bigint(20) NOT NULL COMMENT '索引ID',
	  `goods_spec_id` bigint(20) NOT NULL COMMENT '商品规格索引id',
	  `goods_id` bigint(20) NOT NULL COMMENT '商品id',
	  `spec_goods_serial` varchar(50) NOT NULL COMMENT '规格商品编号',
	  `express_id` bigint(20) NOT NULL COMMENT '快递公司id',
	  `e_state` tinyint(4) NOT NULL DEFAULT '0' COMMENT '状态 0有效  1无效',
	  `creation_by` varchar(30) DEFAULT NULL COMMENT '创建人',
	  `creation_time` datetime DEFAULT NULL COMMENT '创建时间',
	  `update_by` varchar(20) DEFAULT NULL COMMENT '更新人',
	  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
	* */

}
