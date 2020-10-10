package com.framework.loippi.entity.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * @author :ldq
 * @date:2020/9/18
 * @description:dubbo com.framework.loippi.entity.order
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "shop_spirit_order_info")
public class ShopSpiritOrderInfo implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** 订单商品表索引id */
	@Column(id = true, name = "id", updatable = false)
	private Long id;

	/** 订单id */
	@Column(name = "order_id" )
	private Long orderId;

	/** 商品id */
	@Column(name = "goods_id" )
	private Long goodsId;

	/** 规格id */
	@Column(name = "spec_id" )
	private Long specId;

	/** 商品数量 */
	@Column(name = "goods_num" )
	private Integer goodsNum;

	/** 提交状态 0未提交 1已提交 */
	@Column(name = "submit_state" )
	private Integer submitState;

	/** 订单发货状态 0未发货 1已发货 */
	@Column(name = "order_ship_state" )
	private Integer orderShipState;

	@Column(name = "create_time" )
	private Date createTime;

	@Column(name = "upload_time" )
	private Date uploadTime;

	@Column(name = "order_type" )
	private Integer orderType;
}
