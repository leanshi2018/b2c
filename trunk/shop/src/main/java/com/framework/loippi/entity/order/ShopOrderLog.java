package com.framework.loippi.entity.order;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Entity - 订单处理历史表
 * 
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_ORDER_LOG")
public class ShopOrderLog implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** 订单处理历史索引id */
	@Column(id = true, name = "id", updatable = false)
	private Long id;
	
	/** 订单id */
	@Column(name = "order_id" )
	private Long orderId;
	
	/** 订单状态信息 */
	@Column(name = "order_state" )
	private String orderState;
	
	/** 下一步订单状态信息 */
	@Column(name = "change_state" )
	private String changeState;
	
	/** 订单状态描述 */
	@Column(name = "state_info" )
	private String stateInfo;
	
	/** 处理时间 */
	@Column(name = "create_time" )
	private Date createTime;
	
	/** 操作人 */
	@Column(name = "operator" )
	private String operator;
	
}
