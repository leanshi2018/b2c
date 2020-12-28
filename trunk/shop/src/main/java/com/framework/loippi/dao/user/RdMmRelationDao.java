package com.framework.loippi.dao.user;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.pojo.common.MemCensusVo;
import com.framework.loippi.pojo.common.RankNumVo;

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


    MemCensusVo getMemAtotal(HashMap<String, Object> map);

    Long getNoPayCommonMem();

    List<RankNumVo> findRankNum();

    Long findMemEffective();

    Long findNoBuyNum(String time);

    Long findNewVipRankMoreOne();

	Integer findSponCountByMCode(String sponsorCode);

    List<RdMmRelation> selectAllLower(String mmCode);

    List<RdMmRelation> findFreezeMem();

    List<RdMmRelation> findBySponsorCodeRuleOut(String mmCode);
}
