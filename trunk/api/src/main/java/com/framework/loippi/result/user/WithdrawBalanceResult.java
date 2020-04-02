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

	public static WithdrawBalanceResult build1(RdMmBasicInfo basicInfo, Long withdrawAmount){
		WithdrawBalanceResult result = new WithdrawBalanceResult();
		result.setMmCode(basicInfo.getMmCode());
		result.setWithdrawAmount(new BigDecimal(withdrawAmount).divide(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP));
		return result;
	}

}
