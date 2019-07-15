package com.framework.loippi.service;

import org.apache.ibatis.annotations.Param;

import com.framework.loippi.entity.Role;

/**
 * SERVICE - ROLE
 * 
 * @author Loippi Team
 * @version 1.0
 */
public interface RoleService extends GenericService<Role, Long> {

	/**
	 * 查找角色并加载资源列表
	 * 
	 * @param id
	 *            ROLEID
	 * @return
	 */
	Role findRoleAndAcls(@Param("id") Long id);

	/**
	 * 保存角色
	 * 
	 * @param role
	 *            角色
	 * @param ids
	 *            资源ID列表
	 */
	void save(Role role, Long... ids);
	
	
	/**
	 * 修改角色
	 * 
	 * @param role
	 *            角色
	 * @param ids
	 *            资源ID列表
	 */
	void update(Role role, Long... ids);
}
