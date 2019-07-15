package com.framework.loippi.entity.activity;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 订单优惠表Entity
 *
 * @author kwg
 * @version 2016-09-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_ACTIVITY_ORDER")
public class ShopActivityOrder implements GenericEntity {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 优惠资源id
	 */
	private Long id;


	/**
	 * 订单id
	 */
	@Column(name="order_id")
	private Long orderId;


	/**
	 * 活动id
	 */
	@Column(name="activity_id")
	private Long activityId;


	/**
	 * 活动规则id
	 */
	@Column(name="rule_id")
	private Long ruleId;


	/**
	 * 活动名称
	 */
	@Column(name="activity_name")
	private String activityName;


	/**
	 * 优惠资源
	 */
	@Column(name="coupon_source")
	private String couponSource;


	/**
	 * 优惠类型  1活动专场 2时尚界 3港淘 4新人专场 60秒杀 50团购
	 */
	@Column(name="coupon_type")
	private Integer couponType;

	@Column(name="update_time")
	private Date updateTime;

	@Column(name="create_time")
	private Date createTime;

}