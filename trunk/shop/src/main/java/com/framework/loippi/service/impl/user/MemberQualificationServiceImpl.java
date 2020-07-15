package com.framework.loippi.service.impl.user;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.dao.user.MemberQualificationDao;
import com.framework.loippi.entity.user.MemberQualification;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.result.sys.SelfPerformanceJob;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.MemberQualificationService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;

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
    public MemberQualification findByMCodeAndPeriodCode(Paramap putMap) {
        return memberQualificationDao.findByMCodeAndPeriodCode(putMap);
    }

    @Override
    public MemberQualification findByCodeAndPeriod(Paramap put) {
        return memberQualificationDao.findByCodeAndPeriod(put);
    }

    @Override
    public Page findListView(Pageable pageable) {
        PageList<SelfPerformanceJob> result = memberQualificationDao.findListView(pageable.getParameter(), pageable.getPageBounds());
        return new Page<>(result, result.getPaginator().getTotalCount(), pageable);
    }

    @Override
    public List<MemberQualification> findByHighRank4(String periodCode) {
        return memberQualificationDao.findByHighRank4(periodCode);
    }

    @Override
    public Integer findVipNumByMCode(String mCode) {
        return memberQualificationDao.findVipNumByMCode(mCode);
    }

    @Override
    public Integer countByMCode(String mCode) {
        return memberQualificationDao.countByMCode(mCode);
    }
}
