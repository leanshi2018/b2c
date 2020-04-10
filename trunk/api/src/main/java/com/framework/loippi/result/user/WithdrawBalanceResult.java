package com.framework.loippi.result.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import com.framework.loippi.entity.user.RdMmBasicInfo;

/**
 * @author :ldq
 * @date:2020/4/2
 * @description:dubbo com.framework.loippi.result.user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawBalanceResult {

	/**
	 * 会员编号
	 */
	private String mmCode;

	/**
	 * 余额
	 */
	private BigDecimal withdrawAmount;
	/**
	 * 提现手续费（通联收）
	 */
	private BigDecimal withdrawFee;
	/**
	 * 提现上限
	 */
	private BigDecimal upLimitAmount;
	/**
	 * 提现下限
	 */
	private BigDecimal lowerLimitAmount;


	public static WithdrawBalanceResult build1(RdMmBasicInfo basicInfo, Long withdrawAmount){
		WithdrawBalanceResult result = new WithdrawBalanceResult();
		result.setMmCode(basicInfo.getMmCode());
		result.setWithdrawAmount(new BigDecimal(withdrawAmount).divide(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP));
		result.setWithdrawFee(new BigDecimal("1"));
		result.setUpLimitAmount(new BigDecimal("50000"));
		result.setLowerLimitAmount(new BigDecimal("10"));
		return result;
	}

}
