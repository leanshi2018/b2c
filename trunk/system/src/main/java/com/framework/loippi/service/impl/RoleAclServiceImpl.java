package com.framework.loippi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.RoleAclDao;
import com.framework.loippi.entity.RoleAcl;
import com.framework.loippi.service.RoleAclService;

/**
 * Service - ROLE-ACL
 *
 * @author Loippi Team
 * @version 1.0
 */
@Service("roleAclServiceImpl")
public class RoleAclServiceImpl extends GenericServiceImpl<RoleAcl, Long> implements RoleAclService {

    @Autowired
    private RoleAclDao roleAclDao;

    @Autowired
    public void setGenericDao() {
        super.setGenericDao(roleAclDao);
    }

    public void deleteByAclId(Long aclId) {
        roleAclDao.deleteByAclId(aclId);
    }

    @Override
    public void deleteByRoleId(Long roleId) {
        roleAclDao.deleteByRoleId(roleId);
    }

}
