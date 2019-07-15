package com.framework.loippi.entity;

import java.util.Date;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity - 代码生成器生成方案
 * 
 * @author Loippi Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_GEN_SCHEME")
public class GenScheme implements GenericEntity {
	private static final long serialVersionUID = -5306923026754059704L;
	
	/** ID */
	@Column(id = true, name = "ID", updatable = false)
	private Long id;
	
	/** 创建日期 */
	@Column(name = "CREATE_DATE")
	private Date createDate;
	
	/** 创建者 */
	@Column(name = "CREATOR")
	private Long creator;
	
	/** 更新日期 */
	@Column(name = "UPDATE_DATE")
	private Date updateDate;
	
	/** 更新者 */
	@Column(name = "UPDATOR")
	private Long updator;
	
	/** 策略 */
	@Column(name = "STRATEGY")
	private Integer strategy;
	
	/** 生成方案名称 */
	@Column(name = "SCHEME_NAME")
	private String name;
	
	/** 生成模版 */
	@Column(name = "SCHEME_TEMPLATE")
	private Integer template;
	
	/** 生成包路径 */
	@Column(name = "PACKAGE_NAME")
	private String packageName;
	
	/** 生成模块名 */
	@Column(name = "MODULE_NAME")
	private String moduleName;
	
	/** 描述 */
	@Column(name = "DESCRIPTION")
	private String description;
	
	/** 作者 */
	@Column(name = "AUTHOR")
	private String author;
	
	/** 关联业务表 */
	@Column(name = "GEN_TABLE_ID")
	private Long genTableId;
}
