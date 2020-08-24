package com.framework.loippi.service.user;

import com.framework.loippi.entity.user.MemberPrivilege;
import com.framework.loippi.service.GenericService;

import java.util.List;

public interface MemberPrivilegeService extends GenericService<MemberPrivilege, Long> {
    List<MemberPrivilege> findAscTime();
}
