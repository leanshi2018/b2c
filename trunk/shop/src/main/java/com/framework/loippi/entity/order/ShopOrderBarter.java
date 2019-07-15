package com.framework.loippi.entity.order;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Entity - 换货表
 * 
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_ORDER_BARTER")
public class ShopOrderBarter implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** 记录ID */
	@Column(id = true, name = "id", updatable = false)
	private Long id;
	
	/** 订单ID */
	@Column(name = "order_id" )
	private Long orderId;
	
	/** 订单编号 */
	@Column(name = "order_sn" )
	private String orderSn;
	
	/** 申请编号 */
	@Column(name = "barter_sn" )
	private String barterSn;
	
	/** 店铺ID */
	@Column(name = "store_id" )
	private Long storeId;
	
	/** 店铺名称 */
	@Column(name = "store_name" )
	private String storeName;
	
	/** 买家ID */
	@Column(name = "buyer_id" )
	private Long buyerId;
	
	/** 买家会员名 */
	@Column(name = "buyer_name" )
	private String buyerName;
	
	/** 商品ID */
	@Column(name = "goods_id" )
	private Long goodsId;
	
	/** 订单商品ID */
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
	
	/** 订单商品类型:10默认20团购商品30限时折扣商品40组合套装 */
	@Column(name = "order_goods_type" )
	private Integer orderGoodsType;
	
	/** 卖家处理状态:10为待审核,20为同意,30为不同意 */
	@Column(name = "seller_state" )
	private Integer sellerState;
	
	/** 订单锁定类型:1为不用锁定,2为需要锁定,默认为1 */
	@Column(name = "order_lock" )
	private Integer orderLock;
	
	/** 物流状态:10为买家待发货,20为卖家待收货,30卖家已收货,40为买家待收货,50为已完成,默认为0 */
	@Column(name = "goods_state" )
	private Integer goodsState;
	
	/** 添加时间 */
	@Column(name = "create_time" )
	private Date createTime;
	
	/** 卖家处理时间 */
	@Column(name = "seller_time" )
	private Date sellerTime;
	
	/** 原因ID:0为其它 */
	@Column(name = "reason_id" )
	private Long reasonId;
	
	/** 原因内容 */
	@Column(name = "reason_info" )
	private String reasonInfo;
	
	/** 图片 */
	@Column(name = "pic_info" )
	private String picInfo;
	
	/** 申请原因 */
	@Column(name = "buyer_message" )
	private String buyerMessage;
	
	/** 卖家备注 */
	@Column(name = "seller_message" )
	private String sellerMessage;
	
	/** 买家发货物流公司编号 */
	@Column(name = "buyer_express_id" )
	private Long buyerExpressId;
	
	/** 买家发货物流单号 */
	@Column(name = "buyer_invoice_no" )
	private String buyerInvoiceNo;
	
	/** 买家发货物流公司名称 */
	@Column(name = "buyer_express_name" )
	private String buyerExpressName;
	
	/** 买家发货时间 */
	@Column(name = "buyer_ship_time" )
	private Date buyerShipTime;
	
	/** 买家收货延迟时间 */
	@Column(name = "buyer_delay_time" )
	private Date buyerDelayTime;
	
	/** 买家收货时间 */
	@Column(name = "buyer_receive_time" )
	private Date buyerReceiveTime;
	
	/** 买家收货备注 */
	@Column(name = "buyer_receive_message" )
	private String buyerReceiveMessage;
	
	/** 卖家发货物流公司编号 */
	@Column(name = "seller_express_id" )
	private Long sellerExpressId;
	
	/** 卖家发货物流单号 */
	@Column(name = "seller_invoice_no" )
	private String sellerInvoiceNo;
	
	/** 卖家发货物流公司名称 */
	@Column(name = "seller_express_name" )
	private String sellerExpressName;
	
	/** 卖家发货时间 */
	@Column(name = "seller_ship_time" )
	private Date sellerShipTime;
	
	/** 卖家收货延迟时间 */
	@Column(name = "seller_delay_time" )
	private Date sellerDelayTime;
	
	/** 卖家收货时间 */
	@Column(name = "seller_receive_time" )
	private Date sellerReceiveTime;
	
	/** 卖家收货备注 */
	@Column(name = "seller_receive_message" )
	private String sellerReceiveMessage;
	
}
