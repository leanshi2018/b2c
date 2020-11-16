package com.framework.loippi.dao.user;

import java.util.HashMap;
import java.util.List;

import com.framework.loippi.entity.user.MemberQualification;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.mybatis.paginator.domain.PageBounds;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.pojo.common.AddVipGrantTicketVo;
import com.framework.loippi.result.sys.SelfPerformanceJob;
import com.framework.loippi.utils.Paramap;

public interface MemberQualificationDao extends GenericDao<MemberQualification, Long> {
    List<MemberQualification> findBySponsorCodeAndPeriodCode(HashMap<String, Object> map1);

	MemberQualification findByMCodeAndPeriodCode(Paramap putMap);

    MemberQualification findByCodeAndPeriod(Paramap put);

	PageList<SelfPerformanceJob> findListView(Object parameter, PageBounds pageBounds);

	List<MemberQualification> findByHighRank4(String periodCode);

	Integer findVipNumByMCode(String mCode);

	Integer countByMCode(String mCode);

	List<AddVipGrantTicketVo> countAddVipNum();

	List<MemberQualification> findPreIsNullCountAddVipNum();
}
