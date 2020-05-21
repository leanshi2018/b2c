package com.framework.loippi.dao.user;


import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * DAO - RdMmRelation(会员关系状态表)
 * 
 * @author dzm
 * @version 2.0
 */
public interface RdMmRelationDao  extends GenericDao<RdMmRelation, Long> {

    List<RdMmRelation> findBySponsorCode(String mmCode);

    RdMmRelation findBySpoCode(String code);

	void updateRelaSponsorBySponsorCode(Map<String, Object> map);

	Integer findSponCountByMCode(String sponsorCode);
}
