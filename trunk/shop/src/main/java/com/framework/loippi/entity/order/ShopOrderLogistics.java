package com.framework.loippi.entity.order;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity - 订单商品物流
 * 
 * @author dzm
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_ORDER_LOGISTICS")
public class ShopOrderLogistics implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** 主键 */
	@Column(id = true, name = "id", updatable = false)
	private Long id;
	
	/** 订单id */
	@Column(name = "order_id" )
	private Long orderId;
	
	/** 商品id */
	@Column(name = "goods_id" )
	private Long goodsId;
	
	/** 商品名称 */
	@Column(name = "goods_name" )
	private String goodsName;
	
	/** 规格id */
	@Column(name = "spec_id" )
	private Long specId;
	
	/** 规格描述 */
	@Column(name = "spec_info" )
	private String specInfo;
	
	/** 商品类型1-普通2-换购3-组合 */
	@Column(name = "goods_type" )
	private Integer goodsType;
	
	/** 商品图片 */
	@Column(name = "goods_image" )
	private String goodsImage;
	
	/** 商品pv值 */
	@Column(name = "ppv" )
	private java.math.BigDecimal ppv;
	
	/** 零售价 */
	@Column(name = "price" )
	private java.math.BigDecimal price;
	
	/** 配送公司ID */
	@Column(name = "shipping_express_id" )
	private Long shippingExpressId;
	
	/** 配送公司编号 */
	@Column(name = "shipping_express_code" )
	private String shippingExpressCode;
	
	/** 物流单号 */
	@Column(name = "shipping_code" )
	private String shippingCode;

	/** 商品数量 */
	@Column(name = "goods_num" )
	private Integer goodsNum;

	
}
