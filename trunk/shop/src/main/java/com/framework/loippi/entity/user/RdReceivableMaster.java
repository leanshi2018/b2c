package com.framework.loippi.entity.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
@Table(name = "rd_receivable_master")
public class RdReceivableMaster implements GenericEntity {
	private static final long serialVersionUID = 5081846432919091193L;

	/** 会员编号 */
	private String mmCode;
	/** 昵称 */
	private String mmNickName;
	/** 会员欠款余额 */
	private BigDecimal receivableBlance;
	/** 币种 */
	private String currencyCode;
	/** 状态\r\n1：正常\r\n2：异常 */
	private Integer status;
	/** 自动扣工资的百分比 */
	private Float bnsDeductPecent;

}
