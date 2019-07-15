package com.framework.loippi.entity;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Entity - 角色资源
 * 
 * @author Loippi Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "T_SYSTEM_ROLE_ACLS")
public class RoleAcl implements GenericEntity {

	private static final long serialVersionUID = -589247805453595197L;

	/** ROLE ID */
	@Column(name = "ROLE_ID")
	private Long roleId;

	/** ACL ID */
	@Column(name = "ACL_ID")
	private Long aclId;

}
