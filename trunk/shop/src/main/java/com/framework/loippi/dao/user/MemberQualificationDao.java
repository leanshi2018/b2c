package com.framework.loippi.dao.user;

import com.framework.loippi.entity.user.MemberQualification;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.utils.Paramap;

import java.util.HashMap;
import java.util.List;

public interface MemberQualificationDao extends GenericDao<MemberQualification, Long> {
    List<MemberQualification> findBySponsorCodeAndPeriodCode(HashMap<String, Object> map1);

    MemberQualification findByCodeAndPeriod(Paramap put);
}
