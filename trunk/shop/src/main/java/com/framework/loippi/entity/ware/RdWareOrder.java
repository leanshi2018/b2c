package com.framework.loippi.entity.ware;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Table;
import com.framework.loippi.vo.store.OrderGoodsVo;

/**
 * @author :ldq
 * @date:2020/5/28
 * @description:leanshi_member cn.leanshi.model
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_ware_order")
public class RdWareOrder implements GenericEntity {
	private static final long serialVersionUID = 5081846432919091193L;

	@JsonSerialize(using = ToStringSerializer.class)
	private Long id;//id
	private String orderSn;//订单编号
	private String storeId;//自提店铺id(仓库编号)
	private String storeName;//自提店铺名称(仓库名称)
	private String mCode;//会员编号
	private String consigneeName;//负责人姓名（收件人姓名）
	private String warePhone;//电话
	private Integer orderType;//订单类型  1零售订单 2会员订单 3pv订单 4优惠订单 5换购订单 6换货订单 7新会员启动包订单 8调拨订单 9后台发货
	private Integer orderState;//订单状态：0:已取消;5待审核;10:待付款;20:待发货;30:待收货;40:交易完成;50:已提交;60:已确认
	private String creationPeriod;//业务周期YYYY-MM
	private Date createTime;//订单生成时间
	private Date expressTime;//发货时间
	private Integer logisticType;//1快递 2自提
	private Long expressId;//快递公司id(shop_common_express_code的id)
	private String shippingCode;//物流单号
	private String orderDesc;//订单信息
	private String shopOrderSn;//关联商品订单编号（补发货）

	//app查询新添
	private List<OrderGoodsVo> orderGoodsVoList;
	/** 省 */
	private String provinceCode;

	/** 市 */
	private String cityCode;

	/** 区 */
	private String countryCode;

	/** 详细地址 */
	private String wareDetial;

	/** 订单应付金额(现金支付) */
	private BigDecimal orderAmount;

	/** 订单总价格 */
	private BigDecimal orderTotalPrice;

	/** 补偿积分 */
	private BigDecimal compensatePoint;

	/**
	 * 支付方式id
	 * */
	private Long paymentId;
	/**
	 * 支付方式名称代码
	 * */
	private String paymentCode;
	/**
	 * 支付方式名称
	 * */
	private String paymentName;
	/**
	 * 付款状态:0:未付款;1:已付款
	 * */
	private Integer paymentState;
	/**
	 * 支付(付款)时间
	 * */
	private Date paymentTime;
	/**
	 * 是否需要支付（现金或者积分抵扣）
	 * 	  0不需要  1需要
	 * */
	private Integer flagState;
	/**
	 * 支付表id
	 * */
	private Long payId;
	/**
	 * 支付表编号
	 * */
	private String paySn;
	/**
	 * 交易流水号
	 * */
	private String tradeSn;
	/** 订单所用积分数量 */
	private Integer usePointNum;

	/** 积分抵扣金额 */
	private BigDecimal pointRmbNum;
	/** 退款状态:0是无退款,1是退款 */
	private Integer refundState;
	/** 退款金额（现金部分） */
	private BigDecimal refundRmb;
	/** 售后积分 */
	private BigDecimal refundPoint;


	/**
	 * 上一订单状态
	 */
	private Integer prevOrderState;



}
