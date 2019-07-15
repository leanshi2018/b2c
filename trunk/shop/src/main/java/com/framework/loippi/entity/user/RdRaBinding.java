package com.framework.loippi.entity.user;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity - 会员老系统绑定信息
 * 
 * @author dzm
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RD_RA_BINDING")
public class RdRaBinding implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** 老系统会员编号 */
	@Column(name = "RA_CODE" )
	private String raCode;
	
	/** 老系统会员姓名 */
	@Column(name = "RA_NAME" )
	private String raName;
	
	/** 老系统会员昵称 */
	@Column(name = "RA_NICK_NAME" )
	private String raNickName;
	
	/** 老系统会员状态:1：正常2：冻结-1：注销 */
	@Column(name = "RA_STATUS" )
	private Integer raStatus;
	
	/** 老系统身份证类型 */
	@Column(name = "RA_ID_TYPE" )
	private Integer raIdType;
	
	/** 老系统身份证号码 */
	@Column(name = "RA_ID_CODE" )
	private String raIdCode;
	
	/** 老系统推荐人编号 */
	@Column(name = "RA_SPONSOR_CODE" )
	private String raSponsorCode;
	
	/** 老系统推荐人姓名 */
	@Column(name = "RA_SPONSOR_NAME" )
	private String raSponsorName;
	
	/** 绑定状态:0：未绑定1：已绑定 */
	@Column(name = "BINDING_STATUS" )
	private Integer bindingStatus;
	
	/** 绑定的RD会员号 */
	@Column(name = "RD_CODE" )
	private String rdCode;
	
	/** 绑定时间 */
	@Column(name = "BINDING_DATE" )
	private java.util.Date bindingDate;
	
	/** 绑定操作人 */
	@Column(name = "BINDING_BY" )
	private String bindingBy;
	
}
