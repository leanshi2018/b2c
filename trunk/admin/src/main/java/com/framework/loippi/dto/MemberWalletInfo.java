package com.framework.loippi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.framework.loippi.entity.user.RdMmBasicInfo;

/**
 * 会员钱包管理返回参数
 * @author :ldq
 * @date:2020/3/27
 * @description:dubbo com.framework.loippi.dto
 */
@Data
public class MemberWalletInfo {

	/** 会员编号 */
	private String mmCode;
	/** 会员昵称 */
	private String mmNickName;
	/** 手机:绑定的手机 */
	private String mobile;
	/** 会员姓名 */
	private String mmName;
	/** 证件号码 */
	private String idCode;
	/** 0:未认证 1：已认证 */
	private Integer whetherTureName;
	/** 0:未签约 1：已签约  通联支付签约状态 */
	private Integer allInContractStatus;
	/** 会员状态 0无效 1有效  3审核失败 5已锁 7待审核  */
	private Long mmStatus;
	/** 钱包余额  */
	private BigDecimal walletAmount;
	/** 钱包状态 0:正常 1：冻结  */
	private Integer walletStatus;
	/** 冻结额  */
	private BigDecimal freezeAmount;
	/** 银行卡信息  */
	private List<BindCardDto> bindCardDtoList;

	public static MemberWalletInfo build( RdMmBasicInfo rdMmBasicInfo,List<BindCardDto> bindCardDtoList,Long allAmount,Long freezeAmount,String cardNoDecrypt,String name,String phone,Long userState) {
		MemberWalletInfo result = new MemberWalletInfo();
		result.setMmCode(Optional.ofNullable(rdMmBasicInfo.getMmCode()).orElse(""));
		result.setMmNickName(Optional.ofNullable(rdMmBasicInfo.getMmNickName()).orElse(""));
		result.setMobile(Optional.ofNullable(phone).orElse(""));
		result.setMmName(Optional.ofNullable(name).orElse(""));
		result.setIdCode(Optional.ofNullable(cardNoDecrypt).orElse(""));
		result.setWhetherTureName(Optional.ofNullable(rdMmBasicInfo.getWhetherTureName()).orElse(0));
		result.setAllInContractStatus(Optional.ofNullable(rdMmBasicInfo.getAllInContractStatus()).orElse(0));
		result.setWalletAmount(Optional.ofNullable(new BigDecimal(allAmount)).orElse(BigDecimal.ZERO));
		result.setFreezeAmount(Optional.ofNullable(new BigDecimal(freezeAmount)).orElse(BigDecimal.ZERO));
		if (freezeAmount.longValue()<=0 ){
			result.setWalletStatus(0);
		}else {
			result.setWalletStatus(1);
		}
		result.setBindCardDtoList(bindCardDtoList);

		return result;
	}
}
