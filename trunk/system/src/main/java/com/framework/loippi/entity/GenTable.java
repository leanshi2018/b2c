package com.framework.loippi.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity - 代码生成器:业务表
 * 
 * @author Loippi Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_GEN_TABLE")
public class GenTable implements GenericEntity {
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
	
	/** 表名称 */
	@Column(name = "TABLE_NAME")
	private String name;
	
	/** 描述 */
	@Column(name = "DESCRIPTION")
	private String description;
	
	/** 实体类名称 */
	@Column(name = "CLASS_NAME")
	private String className;
	
	/** 关联父表 */
	@Column(name = "PARENT_TABLE")
	private String parentTable;
	
	/** 关联父表外键 */
	@Column(name = "PARENT_TABLE_FK")
	private String parentTableFk;
	
	/** 备注信息 */
	@Column(name = "REMARKS")
	private String remarks;
	
	
	/** 字段列表 */
	private List<GenTableColumn> columns = new ArrayList<GenTableColumn>();
	
	
	
	public String getClassPk(){
		if(CollectionUtils.isNotEmpty(getColumns())){
			for (GenTableColumn genTableColumn : getColumns()) {
				if(genTableColumn.getPk() == 1){return genTableColumn.getJavaField();}
			}
		}
		return null;
	}
}
