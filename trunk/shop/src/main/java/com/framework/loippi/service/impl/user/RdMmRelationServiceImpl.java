package com.framework.loippi.service.impl.user;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.framework.loippi.service.user.RdMmBasicInfoService;
import com.framework.loippi.utils.Digests;
import com.framework.loippi.utils.Paramap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.dao.user.MemberRelationLogDao;
import com.framework.loippi.dao.user.OldSysRelationshipDao;
import com.framework.loippi.dao.user.RdMmRelationDao;
import com.framework.loippi.entity.user.MemberRelationLog;
import com.framework.loippi.entity.user.OldSysRelationship;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdMmRelationService;


/**
 * SERVICE - RdMmRelation(会员关系状态表)
 * 
 * @author dzm
 * @version 2.0
 */
@Service
@Transactional
public class RdMmRelationServiceImpl extends GenericServiceImpl<RdMmRelation, Long> implements RdMmRelationService {
	@Autowired
	private TwiterIdService twiterIdService;
	@Autowired
	private RdMmRelationDao rdMmRelationDao;
	@Autowired
	private MemberRelationLogDao memberRelationLogDao;
	@Autowired
	private  OldSysRelationshipDao oldSysRelationshipDao;
	@Autowired
	private RdMmBasicInfoService rdMmBasicInfoService;
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdMmRelationDao);
	}

	/**
	 * 根据会员关系表和老系统数据中间表，修改会员等级以及会员注册类型，以及中间表数据的修改
	 * @param rdMmRelation
	 * @param oldSysRelationship
	 */
	@Override
	public void badingAndUpgrade(RdMmRelation rdMmRelation, OldSysRelationship oldSysRelationship) throws Exception{
		MemberRelationLog relationLog = new MemberRelationLog();
		relationLog.setId(twiterIdService.getTwiterId());
		relationLog.setRankBefore(rdMmRelation.getRank());
		if(rdMmRelation.getRank()<3){
			rdMmRelation.setRank(3);
			relationLog.setRankAfter(3);
		}else {
			relationLog.setRankAfter(rdMmRelation.getRank());
		}
		relationLog.setNewOldFlagBefore(1);
		relationLog.setNewOldFlagAfter(2);
		relationLog.setCategory(8);
		relationLog.setCreateTime(new Date());
		relationLog.setMCode(rdMmRelation.getMmCode());
		rdMmRelation.setNOFlag(2);
		rdMmRelation.setRaShopYn(1);
		rdMmRelation.setRaStatus(1);
		rdMmRelation.setRaSponsorStatus(0);
		oldSysRelationship.setNYnRegistered(1);
		oldSysRelationship.setNMcode(rdMmRelation.getMmCode());
        oldSysRelationship.setUpdateTime(new Date());
		memberRelationLogDao.insert(relationLog);
		oldSysRelationshipDao.update(oldSysRelationship);
		rdMmRelationDao.update(rdMmRelation);
	}

	@Override
	public void badingAndUpgrade2(RdMmRelation rdMmRelation, OldSysRelationship oldSysRelationship, RdMmBasicInfo basicInfo) throws Exception {
		MemberRelationLog relationLog = new MemberRelationLog();
		relationLog.setId(twiterIdService.getTwiterId());
		relationLog.setRankBefore(rdMmRelation.getRank());
		if(rdMmRelation.getRank()<3){
			rdMmRelation.setRank(3);
			relationLog.setRankAfter(3);
		}else {
			relationLog.setRankAfter(rdMmRelation.getRank());
		}
		relationLog.setNewOldFlagBefore(1);
		relationLog.setNewOldFlagAfter(2);
		relationLog.setCategory(8);
		relationLog.setCreateTime(new Date());
		relationLog.setMCode(rdMmRelation.getMmCode());
		relationLog.setSpoCodeBefore(rdMmRelation.getSponsorCode());
		relationLog.setSpoCodeBefore(basicInfo.getMmCode());
		relationLog.setRaSpoStatusBefore(0);
		relationLog.setRaSpoStatusAfter(1);
		rdMmRelation.setNOFlag(2);
		rdMmRelation.setRaShopYn(1);
		rdMmRelation.setRaStatus(1);
		rdMmRelation.setRaSponsorStatus(1);
		rdMmRelation.setSponsorCode(basicInfo.getMmCode());
		rdMmRelation.setSponsorName(basicInfo.getMmName());
		oldSysRelationship.setNYnRegistered(1);
		oldSysRelationship.setNMcode(rdMmRelation.getMmCode());
		oldSysRelationship.setUpdateTime(new Date());
		memberRelationLogDao.insert(relationLog);
		oldSysRelationshipDao.update(oldSysRelationship);
		rdMmRelationDao.update(rdMmRelation);
	}

	@Override
	public List<RdMmRelation> findBySponsorCode(String mmCode) {
		return rdMmRelationDao.findBySponsorCode(mmCode);
	}

	@Override
	public void updateRelaSponsorBySponsorCode(String mmCode, String sponsorCode, String sponsorName) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("mmCode",mmCode);
		map.put("sponsorCode",sponsorCode);
		map.put("sponsorName",sponsorName);
		rdMmRelationDao.updateRelaSponsorBySponsorCode(map);
	}

	@Override
	public Long findNewVipRankMoreOne() {
		return rdMmRelationDao.findNewVipRankMoreOne();
	}

	public Integer findSponCountByMCode(String sponsorCode) {
		return rdMmRelationDao.findSponCountByMCode(sponsorCode);
	}

	/**
	 * 修改某一手机下下关联所有会员登录密码
	 * @param mobile
	 * @param newpassword
	 */
	@Override
	public void updatePassword(String mobile, String newpassword) {
		List<RdMmBasicInfo> list = rdMmBasicInfoService.findList("mobile",mobile );
		if (list!=null&&list.size()>0){
			for (RdMmBasicInfo basicInfo : list) {
				List<RdMmRelation> relations = rdMmRelationDao.findByParams(Paramap.create().put("mmCode",basicInfo.getMmCode()));
				if(relations!=null&&relations.size()>0){
					RdMmRelation rdMmRelation = relations.get(0);
					rdMmRelation.setLoginPwd(Digests.entryptPassword(newpassword));
					rdMmRelationDao.update(rdMmRelation);
				}
			}
		}
	}
}
