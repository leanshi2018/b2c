package com.framework.loippi.dao.user;

import com.framework.loippi.entity.user.MemberPrivilege;
import com.framework.loippi.mybatis.dao.GenericDao;

import java.util.List;

public interface MemberPrivilegeDao  extends GenericDao<MemberPrivilege, Long> {
    List<MemberPrivilege> findAscTime();
}
