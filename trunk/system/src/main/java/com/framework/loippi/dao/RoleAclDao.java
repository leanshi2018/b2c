package com.framework.loippi.dao;

import org.apache.ibatis.annotations.Param;

import com.framework.loippi.entity.RoleAcl;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * DAO - ROLE-ACLS
 * 
 * @author Loippi Team
 * @version 1.0
 */
public interface RoleAclDao  extends GenericDao<RoleAcl, Long> {
	
	void deleteByAclId(@Param("aclId") Long aclId);
	
	
	void deleteByRoleId(@Param("roleId") Long roleId);
	
}
