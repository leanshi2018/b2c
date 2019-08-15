package com.framework.loippi.dao.user;

import java.util.HashMap;
import java.util.List;

import com.framework.loippi.entity.user.MemberQualification;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.utils.Paramap;

public interface MemberQualificationDao extends GenericDao<MemberQualification, Long> {
    List<MemberQualification> findBySponsorCodeAndPeriodCode(HashMap<String, Object> map1);

	MemberQualification findByMCodeAndPeriodCode(Paramap putMap);
}
