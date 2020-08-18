package com.framework.loippi.service.impl.user;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.framework.loippi.dao.user.RdMmBasicInfoDao;
import com.framework.loippi.entity.user.*;
import com.framework.loippi.service.user.*;
import com.framework.loippi.utils.Digests;
import com.framework.loippi.utils.Paramap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.dao.user.MemberRelationLogDao;
import com.framework.loippi.dao.user.OldSysRelationshipDao;
import com.framework.loippi.dao.user.RdMmRelationDao;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.impl.GenericServiceImpl;


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
	private RdMmBasicInfoDao rdMmBasicInfoDao;
	@Autowired
	private RdMmEditService rdMmEditService;
	@Autowired
	private RdMmLogOutNumService rdMmLogOutNumService;
	@Autowired
	private RdReceiveableDetailService rdReceiveableDetailService;
	@Autowired
	private RdReceivableMasterService rdReceivableMasterService;
	@Autowired
	private RdMmStatusDetailService rdMmStatusDetailService;
	@Autowired
	private RdBonusPaymentService rdBonusPaymentService;
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
		List<RdMmBasicInfo> list = rdMmBasicInfoDao.findByParams(Paramap.create().put("mobile",mobile) );
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

	/**
	 * 注销用户
	 * @param rdMmBasicInfo
	 */
	@Override
	public void cancellation(RdMmBasicInfo rdMmBasicInfo) {
		//1.判断用户是主店会员还是次店会员
		if(rdMmBasicInfo.getMainFlag()==1){
			RdMmBasicInfo main=null;
			List<RdMmBasicInfo> infos = rdMmBasicInfoDao.findByParams(Paramap.create().put("mobile", rdMmBasicInfo.getMobile()));
			for (RdMmBasicInfo info : infos) {
				if(info.getMainFlag()==1){
					main=info;
					continue;
				}
				killMember(info,2);
			}
			//处理主店
			killMember(main,1);
		}
		if(rdMmBasicInfo.getMainFlag()==2){
			killMember(rdMmBasicInfo,1);
		}
	}

	/**
	 * 注销操作
	 * @param rdMmBasicInfo 会员信息
	 * @param flag 主次店标识
	 */
	private void killMember(RdMmBasicInfo rdMmBasicInfo,Integer flag) {
		List<RdMmRelation> relations = rdMmRelationDao.findByParams(Paramap.create().put("mmCode",rdMmBasicInfo.getMmCode()));
		if(relations!=null&&relations.size()>0){
			RdMmRelation relation = relations.get(0);
			//1.修改会员状态为注销
			relation.setMmStatus(2);
			rdMmRelationDao.update(relation);
			//2.修改下线推荐人
			Map<String,Object> map = new HashMap<String,Object>();
			map.put("mmCode",rdMmBasicInfo.getMmCode());
			if(flag==2){//主店注销次店同步注销 推荐人修改为主店的推荐人
				List<RdMmRelation> relations2= rdMmRelationDao.findByParams(Paramap.create().put("mmCode", relation.getSponsorCode()));
				RdMmRelation relation1 = relations2.get(0);
				map.put("sponsorCode",relation1.getSponsorCode());
				map.put("sponsorName",relation1.getSponsorName());
			}
			if(flag==1){//注销修改下属推荐人为其自身推荐人
				map.put("sponsorCode",relation.getSponsorCode());
				map.put("sponsorName",relation.getSponsorName());
			}
			rdMmRelationDao.updateRelaSponsorBySponsorCode(map);
			//3.修改该会员所有待审核的修改信息未驳回
			rdMmEditService.updateByStatusAndMCode(rdMmBasicInfo.getMmCode());
			//4.处理被注销用户昵称手机号
			RdMmLogOutNum rdMmLogOutNum = rdMmLogOutNumService.find(1);
			Integer logoutNum = rdMmLogOutNum.getLogoutNum()+1;
			rdMmLogOutNum.setLogoutNum(logoutNum);
			rdMmLogOutNumService.update(rdMmLogOutNum);
			//手机号码 = 19900000001开始累加
			StringBuilder phone = new StringBuilder(); //构建空的可变字符串
			phone.append("199");
			for (int i=0;i<8-(logoutNum+"").length();i++){
				phone.append("0");
			}
			phone.append(logoutNum+"");
			//昵称= 已注销1+手机号码开始累加
			String nickName = "已注销"+logoutNum+"-"+phone;
			//5.添加修改信息
			RdMmEdit rdMmEdit = new RdMmEdit();
			rdMmEdit.setMmCode(rdMmBasicInfo.getMmCode());
			rdMmEdit.setMmNickNameBefore(rdMmBasicInfo.getMmNickName());
			rdMmEdit.setMmNickNameAfter(nickName);
			rdMmEdit.setMobileBefore(rdMmBasicInfo.getMobile());
			rdMmEdit.setMobileAfter(phone.toString());
			rdMmEdit.setUpdateBy("用户");
			rdMmEdit.setUpdateMemo("用户注销");
			rdMmEdit.setUpdateType(0);
			rdMmEdit.setUpdateTime(new Date());
			rdMmEdit.setReviewMemo("用户注销");
			rdMmEdit.setReviewStatus(3);
			rdMmEditService.save(rdMmEdit);
			//6.修改基础表
			rdMmBasicInfo.setMmNickName(nickName);
			rdMmBasicInfo.setMobile(phone.toString());
			rdMmBasicInfoDao.update(rdMmBasicInfo);
			//7.修改欠款明细 rd_receiveable_detail
			rdReceiveableDetailService.updateNickNameByMCode(nickName,rdMmBasicInfo.getMmCode());
			//8.修改会员欠款主表 rd_receivable_master
			rdReceivableMasterService.updateNickNameByMCode(nickName,rdMmBasicInfo.getMmCode());
			//9.修改会员状态变更明细 rd_mm_status_detail
			rdMmStatusDetailService.updateNickNameByMCode(nickName,rdMmBasicInfo.getMmCode());
			//10.添加会员状态变更明细 rd_mm_status_detail
			RdMmStatusDetail rdMmStatusDetail = new RdMmStatusDetail();
			rdMmStatusDetail.setMmCode(rdMmBasicInfo.getMmCode());
			rdMmStatusDetail.setMmNickName(nickName);
			rdMmStatusDetail.setStatusType("MM");
			rdMmStatusDetail.setStatusBefore(relation.getMmStatus());
			rdMmStatusDetail.setStatusAfter(2);
			rdMmStatusDetail.setUpdateBy("用户");
			rdMmStatusDetail.setUpdateTime(new Date());
			rdMmStatusDetail.setUpdateDesc("用户主动注销");
			rdMmStatusDetailService.save(rdMmStatusDetail);
			//11.修改奖金发放表 rd_bonus_payment
			rdBonusPaymentService.updateNickNameByMCode(nickName,rdMmBasicInfo.getMmCode());
		}
	}
}
