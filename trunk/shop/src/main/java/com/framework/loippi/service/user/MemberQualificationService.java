package com.framework.loippi.service.user;

import java.util.HashMap;
import java.util.List;

import com.framework.loippi.entity.user.MemberQualification;
import com.framework.loippi.pojo.common.AddVipGrantTicketVo;
import com.framework.loippi.service.GenericService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;

public interface MemberQualificationService extends GenericService<MemberQualification, Long> {
    List<MemberQualification> findBySponsorCodeAndPeriodCode(HashMap<String, Object> map1);

	MemberQualification findByMCodeAndPeriodCode(Paramap putMap);

    MemberQualification findByCodeAndPeriod(Paramap put);

	Page findListView(Pageable pageable);

	List<MemberQualification> findByHighRank4(String periodCode);

	Integer findVipNumByMCode(String mCode);

	Integer countByMCode(String mmCode);

	List<AddVipGrantTicketVo> countAddVipNum();

	List<MemberQualification> findPreIsNullCountAddVipNum();
}
