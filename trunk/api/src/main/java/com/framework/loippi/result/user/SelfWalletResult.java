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
	 * 钱包状态
	 */
	private String amountStatus;
	/**
	 * 0:未签约 1：已签约  通联支付签约状态
	 */
	private String allInContractStatus;
	/**
	 * 0:未认证 1：已认证
	 */
	private String whetherTureName;

	public static SelfWalletResult build1(RdMmBasicInfo basicInfo,Long allAmount,Long freezeAmount){
		SelfWalletResult result = new SelfWalletResult();
		result.setMmCode(basicInfo.getMmCode());
		result.setAllAmount(new BigDecimal(allAmount).divide(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP));
		result.setFreezeAmount(new BigDecimal(freezeAmount).divide(new BigDecimal("100")).setScale(2, BigDecimal.ROUND_HALF_UP));
		if (freezeAmount.longValue()<=0 ){
			result.setAmountStatus("正常");
		}else {
			if (allAmount.longValue()==freezeAmount.longValue()){
				result.setAmountStatus("冻结");
			}else if (allAmount.longValue()>freezeAmount.longValue()){
				result.setAmountStatus("部分冻结");//部分冻结
			}else{
				result.setAmountStatus("冻结");
			}
		}
		if (basicInfo.getAllInContractStatus()==null || basicInfo.getAllInContractStatus()!=1){
			result.setAllInContractStatus("未签约");
		}else {
			result.setAllInContractStatus("已签约");
		}
		if (basicInfo.getWhetherTrueName()==null||basicInfo.getWhetherTrueName()!=1){
			result.setWhetherTureName("未激活");
		}else {
			result.setAllInContractStatus("已激活");
		}
		return result;
	}

}
