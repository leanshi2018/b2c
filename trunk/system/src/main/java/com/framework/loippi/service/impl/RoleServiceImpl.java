package com.framework.loippi.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.RoleAclDao;
import com.framework.loippi.dao.RoleDao;
import com.framework.loippi.entity.Role;
import com.framework.loippi.entity.RoleAcl;
import com.framework.loippi.service.RoleService;

/**
 * Service - ROLE
 *
 * @author LinkCity Team
 * @version 3.0
 */
@Service("roleServiceImpl")
public class RoleServiceImpl extends GenericServiceImpl<Role, Long> implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private RoleAclDao roleAclDao;

    @Autowired
    public void setGenericDao() {
        super.setGenericDao(roleDao);
    }

    public Role findRoleAndAcls(Long id) {
        return roleDao.findRoleAndAcls(id);
    }


    @Override
    public Long delete(Long id) {
        roleAclDao.deleteByRoleId(id);
        return roleDao.delete(id);
    }

    public void save(Role role, Long... ids) {
        role.setCreateDate(new Date());
        role.setIsSystem(role.getIsSystem() != null ? role.getIsSystem() : false);
         roleDao.insert(role);
        if (ids != null) {
            for (Long aclId : ids) {
                roleAclDao.insertEntity(new RoleAcl(role.getId(), aclId));
            }
        }
    }

    public void update(Role role, Long... ids) {
        roleDao.update(role);
        roleAclDao.deleteByRoleId(role.getId());
        if (ids != null){
            for (Long aclId : ids) {
                roleAclDao.insertEntity(new RoleAcl(role.getId(), aclId));
            }
        }
    }

}
