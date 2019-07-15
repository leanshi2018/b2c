package com.framework.loippi.service;

import com.framework.loippi.entity.Acl;

import java.util.List;

/**
 * SERVICE - ACL
 *
 * @author Loippi Team
 * @version 1.0
 */
public interface AclService extends GenericService<Acl, Long> {

    /**
     * 主键删除ACL
     */
    Long delete(Long id);

    /**
     * 查找ROOT菜单
     */
    List<Acl> findRoots(Integer platType);

    /**
     * 根据ID获取子菜单
     */
    List<Acl> findChildrens(Integer platType, Long id);
}
