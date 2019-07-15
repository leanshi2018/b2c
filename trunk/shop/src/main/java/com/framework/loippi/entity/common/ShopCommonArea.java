package com.framework.loippi.entity.common;

import com.google.common.collect.Lists;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * Entity - 地区表
 * 
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_COMMON_AREA")
public class ShopCommonArea implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** 索引ID */
	@Column(name = "id" )
	private Long id;
	
	/** 地区名称 */
	@Column(name = "area_name" )
	private String areaName;
	
	/** 地区父ID */
	@Column(name = "area_parent_id" )
	private Long areaParentId;
	
	/** 排序 */
	@Column(name = "area_sort" )
	private Integer areaSort;
	
	/** 地区深度，从1开始 */
	@Column(name = "area_deep" )
	private Integer areaDeep;
	
	/** 是否删除0:未删除;1:已删除 */
	@Column(name = "is_del" )
	private Integer isDel;
	
	/** 序号 */
	@Column(name = "seq_num" )
	private String seqNum;
	
	/**  */
	@Column(name = "create_time" )
	private Date createTime;
	
	/**  */
	@Column(name = "update_time" )
	private Date updateTime;

	/*********************添加*********************/
	/**当前地区的下级地区集合*/
	private List<ShopCommonArea> childern = Lists.newArrayList();


}
