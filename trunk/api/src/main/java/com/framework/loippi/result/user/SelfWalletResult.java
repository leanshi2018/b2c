package com.framework.loippi.result.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import com.framework.loippi.entity.user.RdMmBasicInfo;

/**
 * @author :ldq
 * @date:2020/3/26
 * @description:dubbo com.framework.loippi.result.user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelfWalletResult {


	/**
	 * 会员编号
	 */
	private String mmCode;
	/**
	 * 余额
	 */
	private BigDecimal allAmount;
	/**
	 * 冻结余额
	 */
	private BigDecimal freezeAmount;

	/**
	 * 钱包状态 0.正常 1.未签约 2.未激活 3.冻结
	 */
	private Integer amountStatus;

	public static SelfWalletResult build1(RdMmBasicInfo basicInfo,Long allAmount,Long freezeAmount){
		SelfWalletResult result = new SelfWalletResult();
		result.setMmCode(basicInfo.getMmCode());
		result.setAllAmount(new BigDecimal(allAmount).divide(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP));
		result.setFreezeAmount(new BigDecimal(freezeAmount).divide(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP));

		if (basicInfo.getWhetherTrueName()==null||basicInfo.getWhetherTrueName()!=1){
			result.setAmountStatus(2);
		}else {
			if (freezeAmount.longValue()<=0){
				result.setAmountStatus(3);
			}else {
				if (basicInfo.getAllInContractStatus()==null || basicInfo.getAllInContractStatus()!=1){
					result.setAmountStatus(1);
				}else {
					result.setAmountStatus(0);
				}
			}
		}

		return result;
	}

}
