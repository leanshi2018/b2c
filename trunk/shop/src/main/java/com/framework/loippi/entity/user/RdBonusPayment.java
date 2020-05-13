package com.framework.loippi.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * @author :ldq
 * @date:2020/5/13
 * @description:dubbo com.framework.loippi.entity.user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_bonus_payment")
public class RdBonusPayment implements GenericEntity {
	private static final long serialVersionUID = 5081846432919091193L;

	/** id */
	private Long id;
	/** 业务周期（YYYYMM） */
	private String periodCode;
	/** 会员编号 */
	private String mmCode;
	/** 当期计算出来的奖金(PV) */
	private BigDecimal bonusSum;
	/** 币种 */
	private String currencyCode;
	/** 当期计算出来的奖金(货币) */
	private BigDecimal bonusSumMoney;
	/** 应补发的奖金 */
	private BigDecimal bonusReissue;
	/** 本期处理的扣款合计 */
	private BigDecimal chargeSum;
	/** 应付奖金（总奖金-各项扣款） */
	private BigDecimal payableSum;
	/** 发放标志 */
	private Integer payStatus;
	/** 会员昵称 */
	private String mmNickName;
	/** 批次号 */
	private Long batchNmu;

}
