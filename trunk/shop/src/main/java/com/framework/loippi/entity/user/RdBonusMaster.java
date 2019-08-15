package com.framework.loippi.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * @author :ldq
 * @date:2019/8/15
 * @description:dubbo com.framework.loippi.entity.user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_bonus_master")
public class RdBonusMaster implements GenericEntity {
	private static final long serialVersionUID = 5081846432919091193L;
	@Column(name = "ID_RD_BONUS_MASTER" )
	private Integer idRdBonusMaster;//主键

	@Column(name = "PERIOD_CODE" )
	private String periodCode;//业务周期

	@Column(name = "MM_CODE" )
	private String mCode;//会员编号

	@Column(name = "BONUS_RETAIL" )
	private BigDecimal bonusRetail;//当期全部零售产生的奖励

	@Column(name = "BONUS_NEW_VIP" )
	private BigDecimal  bonusNewVip;//当期推荐新的VIP产生奖励

	@Column(name = "BONUS_DEVP_1" )
	private BigDecimal  bonusDevp1;//市场拓展奖第1代

	@Column(name = "BONUS_DEVP_2" )
	private BigDecimal  bonusDevp2;//市场拓展奖第2代

	@Column(name = "BONUS_DEVP_SHARE" )
	private BigDecimal  bonusDevpShare;//市场拓展奖分红

	@Column(name = "BONUS_DEVP_SUM" )
	private BigDecimal  bonusDevpSum;//市场拓展奖合计

	@Column(name = "BONUS_LD_DIRECT" )
	private BigDecimal  bonusLdDirect;//领导奖直接奖励

	@Column(name = "BONUS_LD_INDIRECT" )
	private BigDecimal  bonusLdIndirect;//领导奖间接奖励

	@Column(name = "BONUS_LD_SUPPORT" )
	private BigDecimal  bonusLdSupport;//领导奖同级支持奖

	@Column(name = "BONUS_LD_SUM" )
	private BigDecimal  bonusLdSum;//领导奖合计

	@Column(name = "CHARGE_SERVICE" )
	private BigDecimal  chargeService;//服务费扣款

	@Column(name = "BONUS_SUM" )
	private BigDecimal  bonusSum;//总奖励合计
}
