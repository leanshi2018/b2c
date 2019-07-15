package com.framework.loippi.dao;

import org.apache.ibatis.annotations.Param;

import com.framework.loippi.entity.Role;
import com.framework.loippi.mybatis.dao.GenericDao;
/**
 * DAO - ROLE
 * 
 * @author Loippi Team
 * @version 1.0
 */
public interface RoleDao  extends GenericDao<Role, Long> {
	
	/**
	 * 查找ROLE并同时加载关联的ACL列表
	 * @param id
	 * @return
	 */
	Role findRoleAndAcls(@Param("id") Long id);
}
