package com.framework.loippi.entity.user;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity - 老用户关系表
 * 
 * @author dzm
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "OLD_SYS_RELATIONSHIP")
public class OldSysRelationship implements GenericEntity {

	private static final long serialVersionUID = 5081846432919091193L;

	/** id，主键自增 */
	@Column(id = true, name = "ID", updatable = false)
	private Integer id;
	
	/** 老系统会员编号 */
	@Column(name = "O_MCODE" )
	private String oMcode;
	
	/** 老系统会员昵称 */
	@Column(name = "O_NICKNAME" )
	private String oNickname;
	
	/** 老系统直接推荐人编号 */
	@Column(name = "O_SPCODE" )
	private String oSpcode;
	
	///** 老系统会员开店状态 1.正常开店 0 临时 -1冻结  -2终止 */
	//@Column(name = "O_STATUS" )
	//private Integer oStatus;
	
	/** 批次号 */
	@Column(name = "BATCH_ID" )
	private String batchId;
	
	/** 创建时间 */
	@Column(name = "CREATION_TIME" )
	private java.util.Date creationTime;
	
	/** 创建者 */
	@Column(name = "CREATION_BY" )
	private String creationBy;
	
	/** 最新修改时间 */
	@Column(name = "UPDATE_TIME" )
	private java.util.Date updateTime;
	
	/** 最新修改者 */
	@Column(name = "UPDATE_BY" )
	private String updateBy;
	
	/** 绑定新系统会员编号 */
	@Column(name = "N_MCODE" )
	private String nMcode;
	
	/** 老系统会员在新系统是否注册 0：未注册  1：已注册 */
	@Column(name = "N_YN_REGISTERED" )
	private Integer nYnRegistered;
	
}
