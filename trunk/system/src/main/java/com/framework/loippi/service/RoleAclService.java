package com.framework.loippi.service;

import org.apache.ibatis.annotations.Param;

import com.framework.loippi.entity.RoleAcl;

/**
 * SERVICE - ROLE-ACL
 *
 * @author Loippi Team
 * @version 1.0
 */
public interface RoleAclService extends GenericService<RoleAcl, Long> {

    /**
     * 根据ACL_ID删除关联关系
     *
     * @param aclId
     */
    void deleteByAclId(@Param("aclId") Long aclId);

    void deleteByRoleId(Long roleId);
}
