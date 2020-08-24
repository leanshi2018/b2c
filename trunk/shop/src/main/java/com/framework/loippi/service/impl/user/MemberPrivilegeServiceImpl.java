package com.framework.loippi.service.impl.user;

import com.framework.loippi.dao.user.MemberPrivilegeDao;
import com.framework.loippi.entity.user.MemberPrivilege;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.MemberPrivilegeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional
public class MemberPrivilegeServiceImpl extends GenericServiceImpl<MemberPrivilege, Long> implements MemberPrivilegeService {
    @Resource
    private MemberPrivilegeDao memberPrivilegeDao;
    @Override
    public List<MemberPrivilege> findAscTime() {
        return memberPrivilegeDao.findAscTime();
    }
}
