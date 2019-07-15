package com.framework.loippi.entity.order;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Entity - 换货处理日志
 * 
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_ORDER_BARTER_LOG")
public class ShopOrderBarterLog implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** 换货处理历史索引id */
	@Column(id = true, name = "id", updatable = false)
	private Long id;
	
	/** 换货表id */
	@Column(name = "barter_id" )
	private Long barterId;
	
	/** 换货状态信息 */
	@Column(name = "barter_state" )
	private String barterState;
	
	/** 下一步换货状态信息 */
	@Column(name = "change_state" )
	private String changeState;
	
	/** 换货状态描述 */
	@Column(name = "state_info" )
	private String stateInfo;
	
	/** 处理时间 */
	@Column(name = "create_time" )
	private Date createTime;
	
	/** 操作人 */
	@Column(name = "operator" )
	private String operator;
	
}
