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
 * Entity - 会员账户交易日志表
 * 
 * @author dzm
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RD_MM_ACCOUNT_LOG")
public class RdMmAccountLog implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** 交易流水号 */
	@Column(id = true, name = "TRANS_NUMBER", updatable = false)
	private Integer transNumber;
	
	/** 批记录号
（一次处理多条记录的需记录批记录号，以备回滚操作） */
	@Column(name = "BATCH_NUMBER" )
	private Integer batchNumber;
	
	/** 会员编号 */
	@Column(name = "MM_CODE" )
	private String mmCode;
	
	/** 会员昵称 */
	@Column(name = "MM_NICK_NAME" )
	private String mmNickName;
	
	/** 交易类型代码 
BA:奖金转入 
RC:充值 
RB:公司补发奖金 
WD:取现 
SP:转购物积分 
RR:归还欠款 
BT:奖励积分转换 
TF:他人转入 
OT:订单退款 
OP:订单支付 
TT:转给他人 
PC:购买商品并评论
AWD:自动提现预扣减积分
CF:自动自提失败退还积分
EG:换购商品 */
	@Column(name = "TRANS_TYPE_CODE" )
	private String transTypeCode;
	
	/** 进账账户类型
SBB：本人积分账户余额
SWB：本人购物积分账户余额
SRB：本人换购积分账户余额 */
	@Column(name = "ACC_TYPE" )
	private String accType;
	
	/** 交易对方类型
CMP：公司
OBB：其他会员奖金余额
OWB：其他会员购物积分
SBB：本人积分账户余额
SWB：本人购物积分余额
BNK：银行（包括第三方支付）
CSH：现金 */
	@Column(name = "TR_SOURCE_TYPE" )
	private String trSourceType;
	
	/** 对方会员编号（如果是会员） */
	@Column(name = "TR_MM_CODE" )
	private String trMmCode;
	
	/** 与交易有关的银行账户信息（RD_MM_BANK表） */
	@Column(name = "TR_BANK_OID" )
	private Integer trBankOid;
	
	/** 与交易有关的订单信息 */
	@Column(name = "TR_ORDER_OID" )
	private Long trOrderOid;
	
	/** 币种 */
	@Column(name = "CURRENCY_CODE" )
	private String currencyCode;
	
	/** 交易前余额 */
	@Column(name = "BLANCE_BEFORE" )
	private BigDecimal blanceBefore;
	
	/** 交易金额 */
	@Column(name = "AMOUNT" )
	private BigDecimal amount;
	
	/** 交易后余额 */
	@Column(name = "BLANCE_AFTER" )
	private BigDecimal blanceAfter;

	/** 当前提现手续费 */
	@Column(name = "PRESENTATION_FEE_NOW" )
	private BigDecimal presentationFeeNow;

	/** 实际提现额 */
	@Column(name = "ACTUAL_WITHDRAWALS" )
	private BigDecimal actualWithdrawals;

	/** 实际提现额 */
	@Column(name = "BACK_WITHDRAWALS" )
	private Integer backWithdrawals;

	/** 交易时间 */
	@Column(name = "TRANS_DATE" )
	private Date transDate;
	
	/** 交易业务周期 */
	@Column(name = "TRANS_PERIOD" )
	private String transPeriod;
	
	/** 交易说明 */
	@Column(name = "TRANS_DESC" )
	private String transDesc;
	
	/** 交易状态
-2：拒绝授权
-1：已取消
1：新单（保存状态）
2：已申请
3：已授权 */
	@Column(name = "STATUS" )
	private Integer status;

	/**
	 *提现交易标志 0：未完成 1：失败  2：成功
	 */
	@Column(name = "ACC_STATUS" )
	private Integer accStatus;

	/** 冲正标记
0：否
1：是（冲正单） */
	@Column(name = "WASHED_YN" )
	private Integer washedYn;
	
	/** 原始单号(冲正单用) */
	@Column(name = "ORIGN_TRANS_NUMBER" )
	private String orignTransNumber;
	
	/** 创建人 */
	@Column(name = "CREATION_BY" )
	private String creationBy;
	
	/** 创建时间 */
	@Column(name = "CREATION_TIME" )
	private Date creationTime;
	
	/** 更新人 */
	@Column(name = "UPDATE_BY" )
	private String updateBy;
	
	/** 更新时间 */
	@Column(name = "UPDATE_TIME" )
	private Date updateTime;
	
	/** 授权人 */
	@Column(name = "AUTOHRIZE_BY" )
	private String autohrizeBy;
	
	/** 授权时间 */
	@Column(name = "AUTOHRIZE_TIME" )
	private Date autohrizeTime;
	
	/** 授权说明（同意或不同意的理由） */
	@Column(name = "AUTOHRIZE_DESC" )
	private String autohrizeDesc;
	
}
