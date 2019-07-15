package com.framework.loippi.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.AclDao;
import com.framework.loippi.dao.RoleAclDao;
import com.framework.loippi.entity.Acl;
import com.framework.loippi.service.AclService;

/**
 * Service - ACL
 *
 * @author LinkCity Team
 * @version 3.0
 */
@Service("aclServiceImpl")
public class AclServiceImpl extends GenericServiceImpl<Acl, Long> implements AclService {

    @Autowired
    private AclDao aclDao;

    @Autowired
    private RoleAclDao roleAclDao;

    @Autowired
    public void setGenericDao() {
        super.setGenericDao(aclDao);
    }


    public Long delete(Long id) {
        roleAclDao.deleteByAclId(id);
        return aclDao.delete(id);
    }


    public List<Acl> findRoots(Integer platType) {
        return aclDao.findRoots(platType);
    }

    public List<Acl> findChildrens(Integer platType, Long id) {
        return aclDao.findChildrens(platType, id);
    }


}
