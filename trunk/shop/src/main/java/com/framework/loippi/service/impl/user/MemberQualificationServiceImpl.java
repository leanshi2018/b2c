package com.framework.loippi.service.impl.user;

import com.framework.loippi.dao.user.MemberQualificationDao;
import com.framework.loippi.dao.user.OldSysRelationshipDao;
import com.framework.loippi.entity.user.MemberQualification;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.MemberQualificationService;
import com.framework.loippi.utils.Paramap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员资格表
 */
@Service
@Transactional
public class MemberQualificationServiceImpl extends GenericServiceImpl<MemberQualification, Long> implements MemberQualificationService {

    @Autowired
    private MemberQualificationDao memberQualificationDao;

    @Override
    public List<MemberQualification> findBySponsorCodeAndPeriodCode(HashMap<String, Object> map1) {
        return memberQualificationDao.findBySponsorCodeAndPeriodCode(map1);
    }

    @Override
    public MemberQualification findByCodeAndPeriod(Paramap put) {
        return memberQualificationDao.findByCodeAndPeriod(put);
    }
}
