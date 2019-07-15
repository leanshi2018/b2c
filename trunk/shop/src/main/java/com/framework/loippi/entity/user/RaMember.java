package com.framework.loippi.entity.user;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity - 
 * 
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RaMember implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/**  */
	@Column(name = "MM_CODE" )
	private String mmCode;
	
	/**  */
	@Column(name = "MM_NAME" )
	private String mmName;
	
	/**  */
	@Column(name = "SPONSOR_CODE" )
	private String sponsorCode;
	
	/**  */
	@Column(name = "SPONSOR_NAME" )
	private String sponsorName;
	
	/**  */
	@Column(name = "O_PASSWORD" )
	private String oPassword;

	private String oldSessionId;
	
}
