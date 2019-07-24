package com.framework.loippi.service.impl.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.dao.user.OldSysRelationshipDao;
import com.framework.loippi.dao.user.RdMmRelationDao;
import com.framework.loippi.entity.user.OldSysRelationship;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdMmRelationService;

import java.util.Date;


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
	private RdMmRelationDao rdMmRelationDao;

	@Autowired
	private  OldSysRelationshipDao oldSysRelationshipDao;
	
	
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
		if(rdMmRelation.getRank()<3){
			rdMmRelation.setRank(3);
		}
		rdMmRelation.setNOFlag(2);
		oldSysRelationship.setNYnRegistered(1);
		oldSysRelationship.setNMcode(rdMmRelation.getMmCode());
        oldSysRelationship.setUpdateTime(new Date());
		oldSysRelationshipDao.update(oldSysRelationship);
		rdMmRelationDao.update(rdMmRelation);
	}

	@Override
	public void badingAndUpgrade2(RdMmRelation rdMmRelation, OldSysRelationship oldSysRelationship, RdMmBasicInfo basicInfo) throws Exception {
		if(rdMmRelation.getRank()<3){
			rdMmRelation.setRank(3);
		}
		rdMmRelation.setNOFlag(2);
		rdMmRelation.setRaSponsorStatus(1);
		rdMmRelation.setSponsorCode(basicInfo.getMmCode());
		rdMmRelation.setSponsorName(basicInfo.getMmName());
		oldSysRelationship.setNYnRegistered(1);
		oldSysRelationship.setNMcode(rdMmRelation.getMmCode());
		oldSysRelationship.setUpdateTime(new Date());
		oldSysRelationshipDao.update(oldSysRelationship);
		rdMmRelationDao.update(rdMmRelation);
	}
}
