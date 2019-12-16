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


}
