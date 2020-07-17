package com.framework.loippi.service.impl.travel;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.dao.ShopCommonMessageDao;
import com.framework.loippi.dao.ShopMemberMessageDao;
import com.framework.loippi.dao.travel.RdTourismComplianceDao;
import com.framework.loippi.dao.travel.RdTravelTicketDao;
import com.framework.loippi.dao.travel.RdTravelTicketDetailDao;
import com.framework.loippi.dao.user.RdMmAccountInfoDao;
import com.framework.loippi.dao.user.RdMmAccountLogDao;
import com.framework.loippi.dao.user.RdMmBasicInfoDao;
import com.framework.loippi.dao.user.RdMmRelationDao;
import com.framework.loippi.dao.user.RdSysPeriodDao;
import com.framework.loippi.entity.ShopCommonMessage;
import com.framework.loippi.entity.ShopMemberMessage;
import com.framework.loippi.entity.travel.RdTourismCompliance;
import com.framework.loippi.entity.travel.RdTravelTicket;
import com.framework.loippi.entity.travel.RdTravelTicketDetail;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.travel.RdTourismComplianceService;

/**
 * @author :ldq
 * @date:2020/7/7
 * @description:dubbo com.framework.loippi.service.impl.common
 */
@Service
@Slf4j
@Transactional
public class RdTourismComplianceServiceImpl extends GenericServiceImpl<RdTourismCompliance, Long> implements RdTourismComplianceService {

	@Resource
	private RdTourismComplianceDao rdTourismComplianceDao;
	@Resource
	private RdTravelTicketDetailDao rdTravelTicketDetailDao;
	@Resource
	private RdTravelTicketDao rdTravelTicketDao;
	@Resource
	private TwiterIdService twiterIdService;
	@Resource
	private RdMmBasicInfoDao rdMmBasicInfoDao;
	@Resource
	private RdMmRelationDao rdMmRelationDao;
	@Resource
	private RdMmAccountInfoDao rdMmAccountInfoDao;
	@Resource
	private RdMmAccountLogDao rdMmAccountLogDao;
	@Resource
	private ShopMemberMessageDao shopMemberMessageDao;
	@Resource
	private ShopCommonMessageDao shopCommonMessageDao;
	@Resource
	private RdSysPeriodDao rdSysPeriodDao;

	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdTourismComplianceDao);
	}

	@Override
	public RdTourismCompliance findByMmCode(String mmCode) {
		return rdTourismComplianceDao.findByMmCode(mmCode);
	}

	@Override
	public void grantTicket(RdTravelTicket rdTravelTicket) {

		Long issueNum = rdTravelTicket.getIssueNum();
		if (issueNum==null){
			issueNum = 0l;
		}

		Long total = 0l;

		//一级奖励达标会员
		List<RdTourismCompliance> oneQualifyList = rdTourismComplianceDao.findOneQualifyList();
		for (RdTourismCompliance rdTourismCompliance : oneQualifyList) {
			RdMmBasicInfo mmBasicInfo = rdMmBasicInfoDao.findByMCode(rdTourismCompliance.getMmCode());

			RdTravelTicketDetail rdTravelTicketDetail = new RdTravelTicketDetail();
			rdTravelTicketDetail.setId(twiterIdService.getTwiterId());
			rdTravelTicketDetail.setTravelId(rdTravelTicket.getId());
			rdTravelTicketDetail.setTravelName(Optional.ofNullable(rdTravelTicket.getTravelName()).orElse(""));
			rdTravelTicketDetail.setTicketPrice(Optional.ofNullable(rdTravelTicket.getTicketPrice()).orElse(BigDecimal.ZERO));
			rdTravelTicketDetail.setTicketSn("T"+twiterIdService.getTwiterId());
			rdTravelTicketDetail.setStatus(0);
			rdTravelTicketDetail.setOwnCode(rdTourismCompliance.getMmCode());
			rdTravelTicketDetail.setOwnNickName(mmBasicInfo.getMmNickName());
			rdTravelTicketDetail.setOwnTime(new Date());
			rdTravelTicketDetail.setImage(Optional.ofNullable(rdTravelTicket.getImage()).orElse(""));

			rdTravelTicketDetailDao.insert(rdTravelTicketDetail);
			rdTourismCompliance.setOneQualify(2);
			rdTourismCompliance.setOneGrantTime(new Date());
			rdTourismComplianceDao.update(rdTourismCompliance);
			total = total+1;
		}

		//二级奖励达标会员
		List<RdTourismCompliance> twoQualifyList = rdTourismComplianceDao.findTwoQualifyList();
		for (RdTourismCompliance rdTourismCompliance : twoQualifyList) {
			RdMmBasicInfo mmBasicInfo = rdMmBasicInfoDao.findByMCode(rdTourismCompliance.getMmCode());
			for (int i=0;i<3;i++){
				RdTravelTicketDetail rdTravelTicketDetail = new RdTravelTicketDetail();
				rdTravelTicketDetail.setId(twiterIdService.getTwiterId());
				rdTravelTicketDetail.setTravelId(rdTravelTicket.getId());
				rdTravelTicketDetail.setTravelName(Optional.ofNullable(rdTravelTicket.getTravelName()).orElse(""));
				rdTravelTicketDetail.setTicketPrice(Optional.ofNullable(rdTravelTicket.getTicketPrice()).orElse(BigDecimal.ZERO));
				rdTravelTicketDetail.setTicketSn("T"+twiterIdService.getTwiterId());
				rdTravelTicketDetail.setStatus(0);
				rdTravelTicketDetail.setOwnCode(rdTourismCompliance.getMmCode());
				rdTravelTicketDetail.setOwnNickName(mmBasicInfo.getMmNickName());
				rdTravelTicketDetail.setOwnTime(new Date());
				rdTravelTicketDetail.setImage(Optional.ofNullable(rdTravelTicket.getImage()).orElse(""));

				rdTravelTicketDetailDao.insert(rdTravelTicketDetail);
				total = total+1;
			}
			rdTourismCompliance.setTwoQualify(2);
			rdTourismCompliance.setTwoGrantTime(new Date());
			rdTourismComplianceDao.update(rdTourismCompliance);
		}

		//三级奖励达标会员
		List<RdTourismCompliance> threeQualifyList = rdTourismComplianceDao.findThreeQualifyList();
		for (RdTourismCompliance rdTourismCompliance : twoQualifyList) {
			RdMmBasicInfo mmBasicInfo = rdMmBasicInfoDao.findByMCode(rdTourismCompliance.getMmCode());
			RdMmAccountInfo accountInfo = rdMmAccountInfoDao.findAccByMCode(rdTourismCompliance.getMmCode());
			BigDecimal bonusBlance = accountInfo.getBonusBlance();

			RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
			rdMmAccountLog.setMmCode(rdTourismCompliance.getMmCode());
			rdMmAccountLog.setMmNickName(mmBasicInfo.getMmNickName());
			rdMmAccountLog.setTransTypeCode("MUB");
			rdMmAccountLog.setAccType("SBB");
			rdMmAccountLog.setTrSourceType("CMP");
			rdMmAccountLog.setBlanceBefore(bonusBlance);
			rdMmAccountLog.setAmount(new BigDecimal("10000"));
			rdMmAccountLog.setBlanceAfter(bonusBlance.add(new BigDecimal("10000")));
			rdMmAccountLog.setTransDate(new Date());
			rdMmAccountLog.setCreationTime(new Date());
			rdMmAccountLog.setAutohrizeTime(new Date());
			String period = rdSysPeriodDao.getSysPeriodService(new Date());
			if(period!=null){
				rdMmAccountLog.setTransPeriod(period);
			}
			rdMmAccountLog.setTransDesc("旅游计划达标三级店奖励");
			rdMmAccountLog.setAutohrizeDesc("旅游计划达标三级店奖励");
			rdMmAccountLog.setStatus(3);
			rdMmAccountLogDao.insert(rdMmAccountLog);
			accountInfo.setBonusBlance(bonusBlance.add(new BigDecimal("10000")));
			rdMmAccountInfoDao.update(accountInfo);
			//4.生成通知消息
			ShopCommonMessage shopCommonMessage=new ShopCommonMessage();
			shopCommonMessage.setSendUid(accountInfo.getMmCode());
			shopCommonMessage.setType(1);
			shopCommonMessage.setOnLine(1);
			shopCommonMessage.setCreateTime(new Date());
			shopCommonMessage.setBizType(2);
			shopCommonMessage.setIsTop(1);
			shopCommonMessage.setCreateTime(new Date());
			shopCommonMessage.setTitle("旅游计划达标三级店奖励");
			shopCommonMessage.setContent("旅游计划达标三级店奖励，"+new BigDecimal("10000").setScale(2,BigDecimal.ROUND_HALF_UP)+"奖励积分到积分账户");
			Long msgId = twiterIdService.getTwiterId();
			shopCommonMessage.setId(msgId);
			shopCommonMessageDao.insert(shopCommonMessage);
			ShopMemberMessage shopMemberMessage=new ShopMemberMessage();
			shopMemberMessage.setBizType(2);
			shopMemberMessage.setCreateTime(new Date());
			shopMemberMessage.setId(twiterIdService.getTwiterId());
			shopMemberMessage.setIsRead(0);
			shopMemberMessage.setMsgId(msgId);
			shopMemberMessage.setUid(Long.parseLong(accountInfo.getMmCode()));
			shopMemberMessageDao.insert(shopMemberMessage);

			rdTourismCompliance.setThreeQualify(2);
			rdTourismCompliance.setThreeGrantTime(new Date());
			rdTourismComplianceDao.update(rdTourismCompliance);
		}

		rdTravelTicket.setIssueNum(issueNum+total);
		rdTravelTicketDao.update(rdTravelTicket);
	}

}
