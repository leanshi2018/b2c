package com.framework.loippi.entity.ware;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

/**
 * Entity - 仓库库存表
 * 
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RD_INVENTORY_WARNING")
public class RdInventoryWarning implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/**  */
	@Column(name = "WARE_CODE" )
	private String wareCode;
	
	/**  */
	@Column(name = "WARE_NAME" )
	private String wareName;
	
	/**  */
	@Column(name = "GOODS_CODE" )
	private String goodsCode;
	
	/**  */
	@Column(name = "GOODS_NAME" )
	private String goodsName;
	
	/** 规格id编号 */
	@Column(name = "SPECIFICATION_ID" )
	private Long specificationId;
	
	/** 规格值 */
	@Column(name = "SPECIFICATIONS" )
	private String specifications;
	
	/**  */
	@Column(name = "INVENTORY" )
	private Integer inventory;

	/** 自提仓库赠品数量    负数为欠数量  正数为多给的数量 */
	@Column(name = "F_INVENTORY" )
	private Integer fInventory;

	/**  */
	@Column(name = "PRECAUTIOUS_LINE" )
	private Integer precautiousLine;
	
}
