package com.framework.loippi.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

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
@Table(name = "rd_receiveable_detail")
public class RdReceiveableDetail implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** 交易流水号 */
	private Long transNumber;
	/** 批记录号\r\n（一次处理多条记录的需记录批记录号，以备回滚操作） */
	private Long batchNumber;
	/** 会员编号 */
	private String mmCode;
	/** 昵称 */
	private String mmNickName;
	/** 交易类型代码\r\nNR:新增欠款\r\nRR:归还欠款 */
	private String trTypeCode;
	/** 交易渠道\r\nCMP：公司\r\nOBB：其他会员奖金余额\r\nOWB：其他会员购物积分\r\nSBB：本人积分账户余额\r\nSWB：本人购物积分余额\r\nBNK：银行（包括第三方支付）\r\nCSH：现金 */
	private String trSourceType;
	/** 与交易有关的银行账户信息（RD_MM_BANK表） */
	private Integer trBankOId;
	/** 币种 */
	private String currencyCode;
	/** 交易前余额 */
	private BigDecimal blanceBefore;
	/** 交易金额 */
	private BigDecimal amount;
	/** 交易后余额 */
	private BigDecimal blanceAfter;
	/** 自动扣工资的百分比 */
	private Float bnsDeductPecent;
	/** 交易时间 */
	private Date transDate;
	/** 交易业务周期 */
	private String transPeriod;
	/** 交易说明 */
	private String transDesc;
	/** 交易标志\r\n-1：取消\r\n1：新单（保存状态）\r\n2：已申请\r\n3：已授权 */
	private Integer status;
	/** 冲正标记\r\n0：否\r\n1：是（冲正单） */
	private Integer washedYn;
	/** 原始单号(冲正单用) */
	private String orignTransNumber;
	/** 创建人 */
	private String creationBy;
	/** 创建时间 */
	private Date creationTime;
	/** 更新人 */
	private String updateBy;
	/** 更新时间 */
	private Date updateTime;
	/** 授权人 */
	private String autohrizeBy;
	/** 授权时间 */
	private Date autohrizeTime;
	/** 授权说明（同意或不同意的理由） */
	private String autohrizeDesc;

}
