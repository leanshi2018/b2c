package com.framework.loippi.service.user;

import com.framework.loippi.entity.user.MemberQualification;
import com.framework.loippi.service.GenericService;

import java.util.HashMap;
import java.util.List;

public interface MemberQualificationService extends GenericService<MemberQualification, Long> {
    List<MemberQualification> findBySponsorCodeAndPeriodCode(HashMap<String, Object> map1);
}
