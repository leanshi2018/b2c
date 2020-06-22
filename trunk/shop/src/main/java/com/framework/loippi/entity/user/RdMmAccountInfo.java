package com.framework.loippi.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * Entity - 会员账户信息
 * 
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RD_MM_ACCOUNT_INFO")
public class RdMmAccountInfo implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** 会员编号 */
	@Column(name = "MM_CODE" )
	private String mmCode;
	
	/** 奖励账户状态:0正常1冻结2未激活 */
	@Column(name = "BONUS_STATUS" )
	private Integer bonusStatus;
	
	/** 奖金币种 */
	@Column(name = "BONUS_CURRENCY" )
	private String bonusCurrency;
	
	/** 奖金账户余额 */
	@Column(name = "BONUS_BLANCE" )
	private BigDecimal bonusBlance;
	
	/** 购物积分账户状态:0正常1冻结2未激活 */
	@Column(name = "WALLET_STATUS" )
	private Integer walletStatus;
	
	/** 购物积分币种 */
	@Column(name = "WALLET_CURRENCY" )
	private String walletCurrency;
	
	/** 购物积分账户余额 */
	@Column(name = "WALLET_BLANCE" )
	private BigDecimal walletBlance;
	
	/** 换购积分账户状态:0正常1冻结2未激活 */
	@Column(name = "REDEMPTION_STATUS" )
	private Integer redemptionStatus;
	
	/** 换购积分币种 */
	@Column(name = "REDEMPTION_CURRENCY" )
	private String redemptionCurrency;
	
	/** 换购积分账户余额 */
	@Column(name = "REDEMPTION_BLANCE" )
	private BigDecimal redemptionBlance;
	
	/** 微信账户 */
	@Column(name = "WECHAT_ACC" )
	private String wechatAcc;
	
	/** 支付宝账户 */
	@Column(name = "ALIPAY_ACC" )
	private String alipayAcc;
	
	/** 支付绑定手机:忘记密码后找回的验证码手机 */
	@Column(name = "PAYMENT_PHONE" )
	private String paymentPhone;
	
	/** 支付密码:加密保存 */
	@Column(name = "PAYMENT_PWD" )
	private String paymentPwd;

	/** 上次提现时间 新注册用户使用注册时间 */
	@Column(name = "LAST_WITHDRAWAL_TIME" )
	private Date lastWithdrawalTime;

	/** 是否开启自动提现  0:未开启 1：已开启*/
	@Column(name = "AUTOMATIC_WITHDRAWAL" )
	private Integer automaticWithdrawal;

	/** 用户设置默认提现金额预留线*/
	@Column(name = "WITHDRAWAL_LINE" )
	private BigDecimal withdrawalLine;
}
