package com.framework.loippi.entity.trade;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity - 售后订单商品表
 * 
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_RETURN_ORDER_GOODS")
public class ShopReturnOrderGoods implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** 售后商品表id */
	@Column(id = true, name = "id", updatable = false)
	private Long id;
	
	/** 售后订单id */
	@Column(name = "return_order_id" )
	private Long returnOrderId;


	/** 更新时间 */
	@Column(name = "update_time" )
	private Date updateTime;
	
	/** 创建时间 */
	@Column(name = "create_time" )
	private Date createTime;
	
	/** 商品ID,全部退款是0 */
	@Column(name = "goods_id" )
	private Long goodsId;
	
	/** 订单商品ID,全部退款是0 */
	@Column(name = "order_goods_id" )
	private Long orderGoodsId;
	
	/** 商品名称 */
	@Column(name = "goods_name" )
	private String goodsName;

	/** 商品数量 */
	@Column(name = "goods_num" )
	private Integer goodsNum;

	/** 商品图片 */
	@Column(name = "goods_image" )
	private String goodsImage;
	
	/** 规格id */
	@Column(name = "spec_id" )
	private Long specId;
	
	/** 规格名称 */
	@Column(name = "spec_info" )
	private String specInfo;

	/** 价格 */
	@Column(name = "price" )
	private BigDecimal price;

	/** 商品类型1-普通2-换购3-组合 */
	@Column(name = "goods_type" )
	private Integer goodsType;

	/** pv */
	@Column(name = "ppv" )
	private BigDecimal ppv;

}
