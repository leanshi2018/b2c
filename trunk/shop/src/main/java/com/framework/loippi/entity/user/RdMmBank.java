package com.framework.loippi.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * Entity - 会员银行卡信息
 * 
 * @author dzm
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RD_MM_BANK")
public class RdMmBank implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/**  */
	@Column(id = true, name = "OID", updatable = false)
	private Integer oid;
	
	/** 会员编号 */
	@Column(name = "MM_CODE" )
	private String mmCode;
	
	/** 账户类型:1.储蓄卡、2.信用卡、3.微信、4.支付宝 */
	@Column(name = "ACC_TYPE" )
	private String accType;
	
	/** 币种 */
	@Column(name = "CURRENCY" )
	private String currency;
	
	/** 账户号码 */
	@Column(name = "ACC_CODE" )
	private String accCode;
	
	/** 账户名 */
	@Column(name = "ACC_NAME" )
	private String accName;
	
	/** 银行名称编码 */
	@Column(name = "BANK_CODE" )
	private String bankCode;
	
	/** 银行详细信息 */
	@Column(name = "BANK_DETAIL" )
	private String bankDetail;

	/** 手机 */
	@Column(name = "MOBILE" )
	private String mobile;
	
	/** 有效期:MM/YY */
	@Column(name = "VALID_THRU" )
	private String validThru;
	
	/** 信用卡CVV2码:3位 */
	@Column(name = "CVV2" )
	private String cvv2;

	/** 身份证号 */
	@Column(name = "ID_CARD_CODE" )
	private String idCardCode;

	/** 身份证正面照片 */
	@Column(name = "ID_CARD_FACADE" )
	private String idCardFacade;

	/** 身份证反面照片 */
	@Column(name = "ID_CARD_BACK" )
	private String idCardBack;

	/** 默认提现卡 */
	@Column(name = "DEFAULTBANK" )
	private Integer defaultbank;
	
	/** 默认支付方式 */
	@Column(name = "PAYMENT_DEFAULT" )
	private String paymentDefault;
	
	/** 默认提现方式 */
	@Column(name = "WITHDRAW_DEFAULT" )
	private String withdrawDefault;
	
	/** 默认充值账户 */
	@Column(name = "RECHARGE_DEFAULT" )
	private String rechargeDefault;

	/** 是否有效  否：0    是：1 */
	@Column(name = "INVALID" )
	private Integer inValid;

	/** 是否签约  否：0    是：1 */
	@Column(name = "BANK_SIGNING" )
	private Integer bankSigning;

	/** 签约状态    签约审核中：1   签约审核通过：2 */
	@Column(name = "SIGNING_STATUS" )
	private Integer signingStatus;

	/** 签约图片 */
	@Column(name = "SIGNING_IMAGE" )
	private String signingImage;

	/** 申请签约时间 */
	@Column(name = "CREATE_TIME" )
	private Date createTime;

	/** 授权人 */
	@Column(name = "AUTOHRIZE_BY" )
	private String autohrizeBy;

	/** 授权时间 */
	@Column(name = "AUTOHRIZE_TIME" )
	private Date autohrizeTime;
}
