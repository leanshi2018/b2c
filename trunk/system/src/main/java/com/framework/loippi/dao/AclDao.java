package com.framework.loippi.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.framework.loippi.entity.Acl;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * DAO - ACL
 *
 * @author Loippi Team
 * @version 1.0
 */
public interface AclDao extends GenericDao<Acl, Long> {

    /**
     * 查找顶级资源列表
     *
     * @return
     */
    List<Acl> findRoots(@Param("platType") Integer platType);


    /**
     * 查找资源列表
     *
     * @return
     */
    List<Acl> findByRoleId(@Param("roleId") Long roleId);

    /**
     * 查找子类资源列表
     *
     * @param id
     * @return
     */
    List<Acl> findChildrens(@Param("platType") Integer platType, @Param("id") Long id);
}
