package com.framework.loippi.service.user;


import java.util.List;

import com.framework.loippi.entity.user.OldSysRelationship;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.service.GenericService;

/**
 * SERVICE - RdMmRelation(会员关系状态表)
 * 
 * @author dzm
 * @version 2.0
 */
public interface RdMmRelationService  extends GenericService<RdMmRelation, Long> {

    void badingAndUpgrade(RdMmRelation rdMmRelation, OldSysRelationship oldSysRelationship) throws Exception;

    void badingAndUpgrade2(RdMmRelation rdMmRelation, OldSysRelationship oldSysRelationship, RdMmBasicInfo basicInfo) throws Exception;

    List<RdMmRelation> findBySponsorCode(String mmCode);

	void updateRelaSponsorBySponsorCode(String mmCode, String sponsorCode, String sponsorName);

    Long findNewVipRankMoreOne();

	Integer findSponCountByMCode(String mmCode);

}
