package com.framework.loippi.param.wallet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author :ldq
 * @date:2020/3/27
 * @description:dubbo com.framework.loippi.dto
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BindCardDto {

	/** 会员编号 */
	private String mmCode;
	/** 银行名称 */
	private String bankName;
	/** 银行卡号 */
	private String bankCardNo;
	/** 账户名 */
	private String accName;
	/** 银行卡类型 1.借记卡 2.信用卡 */
	private Long cardType;
	/** 默认提现卡 是否安全卡 true,false */
	private Boolean isSafeCard;
	/** 绑定状态 1绑定 2解绑 */
	private Long bindState;
}
