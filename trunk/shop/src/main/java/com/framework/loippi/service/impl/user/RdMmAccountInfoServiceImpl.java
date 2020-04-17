package com.framework.loippi.service.impl.user;



import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.ShopCommonMessageDao;
import com.framework.loippi.dao.ShopMemberMessageDao;
import com.framework.loippi.dao.order.ShopOrderDao;
import com.framework.loippi.dao.user.RdMmAccountInfoDao;
import com.framework.loippi.dao.user.RdMmAccountLogDao;
import com.framework.loippi.dao.user.RdMmBasicInfoDao;
import com.framework.loippi.dao.user.RdSysPeriodDao;
import com.framework.loippi.entity.ShopCommonMessage;
import com.framework.loippi.entity.ShopMemberMessage;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdMmAccountInfoService;
import com.framework.loippi.utils.Paramap;

/**
 * SERVICE - RdMmAccountInfo(会员账户信息)
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class RdMmAccountInfoServiceImpl extends GenericServiceImpl<RdMmAccountInfo, Long> implements RdMmAccountInfoService {
	
	@Autowired
	private RdMmAccountInfoDao rdMmAccountInfoDao;
	@Autowired
	private RdMmAccountLogDao rdMmAccountLogDao;
	@Autowired
	private RdSysPeriodDao rdSysPeriodDao;
	@Autowired
	private ShopCommonMessageDao shopCommonMessageDao;
	@Autowired
	private ShopMemberMessageDao shopMemberMessageDao;
	@Autowired
	private RdMmBasicInfoDao rdMmBasicInfoDao;
	@Autowired
	private ShopOrderDao shopOrderDao;
	@Autowired
	private TwiterIdService twiterIdService;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdMmAccountInfoDao);
	}

	@Override
	public Integer saveAccountInfo(RdMmAccountInfo rdMmAccountInfo, Integer integration,Integer type,List<RdMmAccountLog> rdMmAccountLogList,RdMmAccountInfo accentMmAccountInfo) {

		//业务周期
//		rdMmAccountLog.setTransPeriod(DateUtil.dateToStr(new Date(),"yyyy-MM"));
			if (rdMmAccountInfo!=null){
				rdMmAccountInfoDao.update(rdMmAccountInfo);
			}
		if (accentMmAccountInfo!=null){
			rdMmAccountInfoDao.update(accentMmAccountInfo);
		}
		String period = rdSysPeriodDao.getSysPeriodService(new Date());
		rdMmAccountLogList.get(0).setTransPeriod(period);
				rdMmAccountLogDao.insert(rdMmAccountLogList.get(0));
			if (rdMmAccountLogList.size()>1){
				rdMmAccountLogList.get(1).setTransPeriod(period);
				rdMmAccountLogDao.insert(rdMmAccountLogList.get(1));
			}
            return rdMmAccountLogList.get(0).getTransNumber();
	}

	@Override
	public Integer saveAccountInfoNew(RdMmAccountInfo rdMmAccountInfo, Double integration, int bop, List<RdMmAccountLog> rdMmAccountLogList, RdMmAccountInfo accentMmAccountInfo, ArrayList<ShopCommonMessage> shopCommonMessages, ArrayList<ShopMemberMessage> shopMemberMessages) {
		if (accentMmAccountInfo!=null){
			rdMmAccountInfoDao.update(accentMmAccountInfo);
		}
		if (rdMmAccountInfo!=null){
			rdMmAccountInfoDao.update(rdMmAccountInfo);
		}
		String period = rdSysPeriodDao.getSysPeriodService(new Date());
		rdMmAccountLogList.get(0).setTransPeriod(period);
		rdMmAccountLogDao.insert(rdMmAccountLogList.get(0));
		if (rdMmAccountLogList.size()>1){
			rdMmAccountLogList.get(1).setTransPeriod(period);
			rdMmAccountLogDao.insert(rdMmAccountLogList.get(1));
		}
		if(shopCommonMessages!=null&&shopCommonMessages.size()>0){
			for (ShopCommonMessage shopCommonMessage : shopCommonMessages) {
				shopCommonMessageDao.insert(shopCommonMessage);
			}
		}
		if(shopMemberMessages!=null&&shopMemberMessages.size()>0){
			for (ShopMemberMessage shopMemberMessage : shopMemberMessages) {
				shopMemberMessageDao.insert(shopMemberMessage);
			}
		}
		return rdMmAccountLogList.get(0).getTransNumber();
	}

	@Override
	public void saveAccountInfo2(RdMmAccountInfo rdMmAccountInfo, int pui, List<RdMmAccountLog> rdMmAccountLogList, RdMmAccountInfo accentMmAccountInfo, ShopCommonMessage shopCommonMessage, ShopMemberMessage shopMemberMessage) {
		if (rdMmAccountInfo!=null){
			rdMmAccountInfoDao.update(rdMmAccountInfo);
		}
		if (accentMmAccountInfo!=null){
			rdMmAccountInfoDao.update(accentMmAccountInfo);
		}
		String period = rdSysPeriodDao.getSysPeriodService(new Date());
		rdMmAccountLogList.get(0).setTransPeriod(period);
		rdMmAccountLogDao.insert(rdMmAccountLogList.get(0));
		if (rdMmAccountLogList.size()>1){
			rdMmAccountLogList.get(1).setTransPeriod(period);
			rdMmAccountLogDao.insert(rdMmAccountLogList.get(1));
		}
		shopCommonMessageDao.insert(shopCommonMessage);
		shopMemberMessageDao.insert(shopMemberMessage);
	}

	@Override
	public void updateAddBonusBlance(String mmCode, BigDecimal bonusBlance) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("mmCode",mmCode);
		map.put("bonusBlance",bonusBlance);
		rdMmAccountInfoDao.updateAddBonusBlance(map);
	}

	@Override
	public List<RdMmAccountInfo> findByMCode(String mCode) {
		return rdMmAccountInfoDao.findByMCode(mCode);
	}

	@Override
	public List<RdMmAccountInfo> findLastWithdrawalOneHundred(BigDecimal acc) {
		List<RdMmAccountInfo> lastWithdrawalOneHundred = rdMmAccountInfoDao.findLastWithdrawalOneHundred(acc);
		return lastWithdrawalOneHundred;
	}

	@Override
	public void reduceAcc(ShopOrder shopOrder, RdMmAccountInfo accountInfo, BigDecimal acc) {
		if(acc.compareTo(BigDecimal.ZERO)==0){//如果acc等于0 默认到公司小B账户0.01元 积分不进行处理
			shopOrder.setCutStatus(5);
			shopOrder.setCutGetId(accountInfo.getMmCode());
			shopOrder.setCutAmount(new BigDecimal("0.01"));
			shopOrder.setCutAcc(new BigDecimal("0.01"));
			shopOrderDao.update(shopOrder);
		}
		if(acc.compareTo(BigDecimal.ZERO)==1){
			RdMmAccountLog rdMmAccountLog = new RdMmAccountLog();
			rdMmAccountLog.setMmCode(accountInfo.getMmCode());
			List<RdMmBasicInfo> basicInfos = rdMmBasicInfoDao.findByParams(Paramap.create().put("mmCode",accountInfo.getMmCode()));
			rdMmAccountLog.setMmNickName(basicInfos.get(0).getMmNickName());
			rdMmAccountLog.setTransTypeCode("AWD");
			rdMmAccountLog.setAccType("SBB");
			rdMmAccountLog.setTrSourceType("BNK");
			rdMmAccountLog.setTrOrderOid(shopOrder.getId());
			rdMmAccountLog.setBlanceBefore(accountInfo.getBonusBlance());
			rdMmAccountLog.setAmount(acc);
			rdMmAccountLog.setBlanceAfter(accountInfo.getBonusBlance().subtract(acc));
			rdMmAccountLog.setTransDate(new Date());
			String period = rdSysPeriodDao.getSysPeriodService(new Date());
			if(period!=null){
				rdMmAccountLog.setTransPeriod(period);
			}
			rdMmAccountLog.setPresentationFeeNow(BigDecimal.ZERO);
			rdMmAccountLog.setActualWithdrawals(acc);
			rdMmAccountLog.setTransDesc("平台订单支付自动分账提现");
			rdMmAccountLog.setAutohrizeDesc("平台订单支付自动分账提现");
			rdMmAccountLog.setStatus(3);
			rdMmAccountLog.setAccStatus(0);
			rdMmAccountLogDao.insert(rdMmAccountLog);
			//3.扣减用户积分
			accountInfo.setBonusBlance(accountInfo.getBonusBlance().subtract(acc));
			accountInfo.setLastWithdrawalTime(new Date());
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
			shopCommonMessage.setTitle("自动提现积分预扣减通知");
			shopCommonMessage.setContent("已预扣减"+acc+"奖励积分，十天后发放到钱包，如发放失败，将返回积分账户，具体请查询积分明细");
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
			//5.修改订单分账相关信息
			shopOrder.setCutStatus(5);
			shopOrder.setCutGetId(accountInfo.getMmCode());
			shopOrder.setCutAmount(acc);
			shopOrder.setCutAcc(acc);
			shopOrderDao.update(shopOrder);
		}
	}

	@Override
	public Long updateStatus(RdMmAccountInfo rdMmAccountInfo) {
		return rdMmAccountInfoDao.updateStatus(rdMmAccountInfo);
	}
}
