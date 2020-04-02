package com.framework.loippi.entity.walet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * 提现信息（通联）
 * @author :ldq
 * @date:2020/4/2
 * @description:dubbo com.framework.loippi.entity.walet
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_mm_withdraw_log")
public class RdMmWithdrawLog implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	@Column(id = true, name = "id")
	private Long id;

	@Column(name = "withdraw_sn" )
	private String withdrawSn;//提现订单号 'W313153514313'

	@Column(name = "mm_code" )
	private String mCode;//会员编号

	@Column(name = "withdraw_amount" )
	private BigDecimal withdrawAmount;//订单金额（包含手续费）

	@Column(name = "withdraw_bank" )
	private String withdrawBank;//提现银行卡

	@Column(name = "withdraw_status" )
	private Integer withdrawStatus;//提现状态 0：成功 1：失败 2.申请中

	@Column(name = "withdraw_time" )
	private Date withdrawTime;//提现申请时间

	@Column(name = "tl_order_no" )
	private String tlOrderNo;//通商云订单号

	@Column(name = "withdraw_memo" )
	private String withdrawMemo;//提现信息



}
