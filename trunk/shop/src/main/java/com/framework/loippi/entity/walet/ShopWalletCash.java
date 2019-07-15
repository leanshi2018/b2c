package com.framework.loippi.entity.walet;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity - 预存款提现记录表
 * 
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_WALLET_CASH")
public class ShopWalletCash implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** 自增编号 */
	@Column(id = true, name = "id", updatable = false)
	private Long id;
	
	/** 记录唯一标示 */
	@Column(name = "pdc_sn" )
	private String pdcSn;
	
	/** 会员编号 */
	@Column(name = "pdc_member_id" )
	private Long pdcMemberId;
	
	/** 会员名称 */
	@Column(name = "pdc_member_name" )
	private String pdcMemberName;
	
	/** 金额 */
	@Column(name = "pdc_amount" )
	private BigDecimal pdcAmount;

    /** 手续费金额 */
    @Column(name = "service_amount" )
    private BigDecimal serviceAmount;
	
	/** 收款银行 */
	@Column(name = "pdc_bank_name" )
	private String pdcBankName;
	
	/** 收款账号 */
	@Column(name = "pdc_bank_no" )
	private String pdcBankNo;
	
	/**开户支行 */
	@Column(name = "pdc_bank_subbranch" )
	private String pdcBankSubbranch;

    /** 开户人姓名 */
    @Column(name = "pdc_bank_name" )
    private String pdcBankUser;
	
	/** 添加时间 */
	@Column(name = "create_time" )
	private Date createTime;

	/** 付款时间 */
	@Column(name = "pdc_payment_time" )
	private Date pdcPaymentTime;
	
	/** 提现支付状态 0默认1支付完成 */
	@Column(name = "pdc_payment_state" )
	private String pdcPaymentState;
	
	/** 支付管理员 */
	@Column(name = "pdc_payment_admin" )
	private Long pdcPaymentAdmin;

    /**提现类型1为支付，2银行 */
    @Column(name = "pdc_type" )
    private String pdcType;


    private Date searchStartTime;
	private Date searchEndTime;
	@Column(name = "lg_desc")
	private String lgDesc;

    @Column(name = "member_desc")
    private String memberLgDesc;
	/**
	 * 店铺id
	 */
	@Column(name = "store_id")
	private Long storeId;
}
