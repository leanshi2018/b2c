package com.framework.loippi.entity.integration;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity - 积分规则设置表
 * 
 * @author dzm
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RD_MM_INTEGRAL_RULE")
public class RdMmIntegralRule implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** id */
	@Column(id = true, name = "RID", updatable = false)
	private Integer rid;
	
	/** 返佣计入奖励积分比例 */
	@Column(name = "RS_COUNT_BONUS_POINT" )
	private Integer rsCountBonusPoint;
	
	/** 奖励积分提现最低限额 */
	@Column(name = "BONUS_POINT_WD_LIMIT" )
	private Integer bonusPointWdLimit;
	
	/** 奖励积分提现手续费 */
	@Column(name = "BONUS_POINT_WD" )
	private Integer bonusPointWd;
	
	/** 奖励积分转入购物积分比例 */
	@Column(name = "BONUS_POINT_SHOPPING" )
	private Integer bonusPointShopping;
	
	/** 购物积分购物比例 */
	@Column(name = "SHOPPING_POINT_SR" )
	private Integer shoppingPointSr;
	
	/** 购物积分转账手续费 */
	@Column(name = "TRANKS_SHOPPING_POINT" )
	private Integer tranksShoppingPoint;
	
}
