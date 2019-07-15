package com.framework.loippi.entity;

import java.util.Date;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * Entity - 代码生成器:表字段
 * 
 * @author Loippi Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_GEN_TABLE_COLUMN")
public class GenTableColumn implements GenericEntity {
	
	private static final long serialVersionUID = -6433370184013560940L;

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
	
	/** 归属表id*/
	@Column(name = "GEN_TABLE_ID")
	private Long genTableId;
	
	/** 名称*/
	@Column(name = "NAME")
	private String name;
	
	/** 描述*/
	@Column(name = "COMMENTS")
	private String comments;
	
	/** JDBC类型*/
	@Column(name = "JDBC_TYPE")
	private String jdbcType;
	
	/** mybatisJDBC类型*/
	@Column(name = "MYBATIS_JDBC_TYPE")
	private String mybatisJdbcType;
	
	/** JAVA类型*/
	@Column(name = "JAVA_TYPE")
	private String javaType;
	
	/** JAVA字段名*/
	@Column(name = "JAVA_FIELD")
	private String javaField;
	
	/** 是否主键(1：是、0：否)*/
	@Column(name = "IS_PK")
	private Integer pk = 0;
	
	/** 是否可为空(1：是、0：否)*/
	@Column(name = "IS_NULL")
	private Integer nullable = 0;
	
	/** 是否为插入字段(1：是、0：否)*/
	@Column(name = "IS_INSERT")
	private Integer insert = 0;
	
	/** 是否编辑字段(1：是、0：否)*/
	@Column(name = "IS_EDIT")
	private Integer edit = 0;
	
	/** 是否列表字段(1：是、0：否)*/
	@Column(name = "IS_LIST")
	private Integer list = 0;
	
	/** isQuery*/
	@Column(name = "IS_QUERY")
	private Integer query = 0;
	
	/** 查询方式（等于、不等于、大于、小于、范围、左LIKE、右LIKE、左右LIKE）*/
	@Column(name = "QUERY_TYPE")
	private String queryType = "=";
	
	/** 字段生成方案（文本框、文本域、下拉框、复选框、单选框、字典选择、人员选择、部门选择、区域选择）*/
	@Column(name = "SHOW_TYPE")
	private String showType;
	
	/** 排序*/
	@Column(name = "SORTS")
	private Integer sort;

	/** 备注*/
	@Column(name = "REMARKS")
	private String remarks;
}
