package com.framework.loippi.entity;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Entity - 系统日志
 * 
 * @author Loippi Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_SYSTEM_LOGS")
public class Log implements GenericEntity {
	private static final long serialVersionUID = -4494144902110236826L;
	
	/** "日志内容"属性名称 */
	public static final String LOG_CONTENT_ATTRIBUTE_NAME = Log.class.getName() + ".CONTENT";
	
	/** ID */
	@Column(id = true, name = "ID", updatable = false)
	private Long id;

	/** 创建日期 */
	@Column(name = "CREATE_DATE")
	private Date createDate;
	
	
	/** 操作 */
	@Column(name = "OPERATIONS")
	private String operation;

	/** 操作员 */
	@Column(name = "OPERATOR")
	private String operator;

	/** 内容 */
	@Column(name = "CONTENT")
	private String content;

	/** 请求参数 */
	@Column(name = "PARAMETER")
	private String parameter;

	/** IP */
	@Column(name = "IP")
	private String ip;

	/**页面字段*/
	/**开始时间*/
	private Date beginDate;
	/**结束时间*/
	private Date endDate;

	/** 角色名称 */
	private String roleName;

}
