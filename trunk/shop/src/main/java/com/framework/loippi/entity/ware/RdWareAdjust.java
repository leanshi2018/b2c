package com.framework.loippi.entity.ware;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity - 发货单表
 * 
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RD_WARE_ADJUST")
public class RdWareAdjust implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** ID */
	@Column(id = true, name = "WID", updatable = false)
	private Integer wid;
	
	/** 仓库代码 */
	@Column(name = "WARE_CODE" )
	private String wareCode;
	
	/** 仓库名称 */
	@Column(name = "WARE_NAME" )
	private String wareName;
	
	/** 调整类型 采购入库：PAW盈盘入库：PFW其他入库：OAW销售出库：SOT盘亏出库：LOT其他出库：OOT */
	@Column(name = "ADJUST_TYPE" )
	private String adjustType;
	
	/** 附件地址 */
	@Column(name = "ATTACH_ADD" )
	private String attachAdd;
	
	/** 状态-2：拒绝-1：已取消1：新单（保存状态）2：待审3：已授权 */
	@Column(name = "STATUS" )
	private Integer status;
	
	/** 入库金额 */
	@Column(name = "WARE_AMOUNT" )
	private java.math.BigDecimal wareAmount;
	
	/** 授权人 */
	@Column(name = "AUTOHRIZE_BY" )
	private String autohrizeBy;
	
	/** 授权时间 */
	@Column(name = "AUTOHRIZE_TIME" )
	private java.util.Date autohrizeTime;
	
	/** 授权说明（同意或不同意的理由） */
	@Column(name = "AUTOHRIZE_DESC" )
	private String autohrizeDesc;
	
}
