package com.framework.loippi.service.impl.user;



import java.math.BigDecimal;
import java.util.*;

import com.framework.loippi.entity.user.MemberPrivilege;
import com.framework.loippi.service.user.MemberPrivilegeService;
import com.framework.loippi.utils.Digests;
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

import javax.annotation.Resource;

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
	@Resource
	private MemberPrivilegeService memberPrivilegeService;
	
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
			Date date = new Date();
			MemberPrivilege memberPrivilege = memberPrivilegeService.find("mmCode",accountInfo.getMmCode());
			if(memberPrivilege!=null){
				memberPrivilege.setLastWithdrawalTime(date);
				memberPrivilegeService.update(memberPrivilege);
			}
			accountInfo.setBonusBlance(accountInfo.getBonusBlance().subtract(acc));
			accountInfo.setLastWithdrawalTime(date);
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

	/**
	 * 修改主店相关所有次店积分支付密码
	 * @param mmBasicInfo
	 * @param newpassword
	 */
	@Override
	public void updatePayPassword(RdMmBasicInfo mmBasicInfo, String newpassword) {
		String mobile = mmBasicInfo.getMobile();
		List<RdMmBasicInfo> list = rdMmBasicInfoDao.findByParams(Paramap.create().put("mobile", mobile));
		if(list!=null&&list.size()>0){
			for (RdMmBasicInfo basicInfo : list) {
				String mmCode = basicInfo.getMmCode();
				RdMmAccountInfo accountInfo = rdMmAccountInfoDao.findAccByMCode(mmCode);
				if(accountInfo!=null){
					accountInfo.setPaymentPwd(Digests.entryptPassword(newpassword));
					rdMmAccountInfoDao.update(accountInfo);
				}
			}
		}
	}

	/**
	 * 商户提现
	 * @param rdMmAccountInfo 积分信息
	 * @param amount 提现金额
	 * @param companyWithdrawalRate 手续费税率
	 * @return
	 */
	@Override
	public HashMap<String, Object> companyDeposit(RdMmAccountInfo rdMmAccountInfo, BigDecimal amount, BigDecimal companyWithdrawalRate,String image) {
		HashMap<String, Object> map = new HashMap<>();
		RdMmBasicInfo basicInfo = rdMmBasicInfoDao.findByMCode(rdMmAccountInfo.getMmCode());
		Optional<RdMmBasicInfo> optional = Optional.ofNullable(basicInfo);
		//1.生成商户提现积分日志
		RdMmAccountLog log = new RdMmAccountLog();
		log.setMmCode(rdMmAccountInfo.getMmCode());
		log.setMmNickName(optional.map(RdMmBasicInfo::getMmNickName).orElse(""));
		log.setTransTypeCode("WD");
		log.setAccType("SBB");
		log.setTrSourceType("BNK");
		log.setTrMmCode(rdMmAccountInfo.getMmCode());
		log.setBlanceBefore(rdMmAccountInfo.getBonusBlance());
		log.setAmount(amount);
		BigDecimal bonusAfter = rdMmAccountInfo.getBonusBlance().subtract(amount);
		log.setBlanceAfter(bonusAfter);
		log.setPresentationFeeNow(companyWithdrawalRate);
		BigDecimal bigDecimal = new BigDecimal("100.00");
		BigDecimal actualWithdrawals = (bigDecimal.subtract(companyWithdrawalRate)).multiply(new BigDecimal("0.01")).multiply(amount).setScale(2, BigDecimal.ROUND_HALF_UP);
		log.setActualWithdrawals(actualWithdrawals);
		log.setTransDate(new Date());
		String period = rdSysPeriodDao.getSysPeriodService(new Date());
		if(period!=null){
			log.setTransPeriod(period);
		}else {
			log.setTransPeriod("");
		}
		log.setTransDesc("商户提现");
		log.setStatus(2);
		log.setAccStatus(0);
		log.setCreationBy(optional.map(RdMmBasicInfo::getMmNickName).orElse(""));
		log.setCreationTime(new Date());
		log.setInvoiceImage(image);
		rdMmAccountLogDao.insert(log);
		//2.扣减积分
		rdMmAccountInfo.setBonusBlance(bonusAfter);
		rdMmAccountInfoDao.update(rdMmAccountInfo);
		map.put("amount",amount);
		map.put("rate",companyWithdrawalRate);
		map.put("actualAmount",actualWithdrawals);
		map.put("bonusAfter",bonusAfter);
		return map;
	}

	@Override
	public HashMap<String, Object> bopTransSure(RdMmAccountInfo rdMmAccountInfo, RdMmAccountInfo acceptAccountInfo, BigDecimal total, String pwd, String message) {
		HashMap<String, Object> map = new HashMap<>();
		RdMmBasicInfo outBasic = rdMmBasicInfoDao.findByMCode(rdMmAccountInfo.getMmCode());
		RdMmBasicInfo inBasic = rdMmBasicInfoDao.findByMCode(acceptAccountInfo.getMmCode());
		Long batchNum = twiterIdService.getTwiterId();
		//1.扣减积分
		//1.1扣减转账人
		rdMmAccountInfo.setBonusBlance(rdMmAccountInfo.getBonusBlance().subtract(total).setScale(2,BigDecimal.ROUND_HALF_UP));
		rdMmAccountInfoDao.update(rdMmAccountInfo);
		//1.2增加收款人
		acceptAccountInfo.setBonusBlance(acceptAccountInfo.getBonusBlance().add(total).setScale(2,BigDecimal.ROUND_HALF_UP));
		rdMmAccountInfoDao.update(acceptAccountInfo);
		//2.生成积分变更日志
		//2.1生成转账人积分扣减日志
		String period = rdSysPeriodDao.getSysPeriodService(new Date());
		RdMmAccountLog outLog = new RdMmAccountLog();
		outLog.setBatchNumber(batchNum);
		outLog.setMmCode(rdMmAccountInfo.getMmCode());
		outLog.setMmNickName(outBasic.getMmNickName());
		outLog.setTransTypeCode("TT");
		outLog.setAccType("SBB");
		outLog.setTrSourceType("OBB");
		outLog.setTrMmCode(acceptAccountInfo.getMmCode());
		outLog.setBlanceBefore(rdMmAccountInfo.getBonusBlance());
		outLog.setAmount(total);
		BigDecimal bonusAfter = rdMmAccountInfo.getBonusBlance().subtract(total);
		outLog.setBlanceAfter(bonusAfter);
		outLog.setTransDate(new Date());
		if(period!=null){
			outLog.setTransPeriod(period);
		}else {
			outLog.setTransPeriod("");
		}
		outLog.setTransDesc("会员奖励积分转出");
		outLog.setStatus(3);
		outLog.setCreationBy(outBasic.getMmNickName());
		outLog.setCreationTime(new Date());
		rdMmAccountLogDao.insert(outLog);
		//2.2生成收款人积分增加日志
		RdMmAccountLog inLog = new RdMmAccountLog();
		inLog.setBatchNumber(batchNum);
		inLog.setMmCode(acceptAccountInfo.getMmCode());
		inLog.setMmNickName(inBasic.getMmNickName());
		inLog.setTransTypeCode("TF");
		inLog.setAccType("SBB");
		inLog.setTrSourceType("OBB");
		inLog.setTrMmCode(rdMmAccountInfo.getMmCode());
		inLog.setBlanceBefore(acceptAccountInfo.getBonusBlance());
		inLog.setAmount(total);
		BigDecimal bonusAfter1 = rdMmAccountInfo.getBonusBlance().add(total);
		inLog.setBlanceAfter(bonusAfter1);
		inLog.setTransDate(new Date());
		if(period!=null){
			inLog.setTransPeriod(period);
		}else {
			inLog.setTransPeriod("");
		}
		inLog.setTransDesc("会员奖励积分转入");
		inLog.setStatus(3);
		inLog.setCreationBy(outBasic.getMmNickName());
		inLog.setCreationTime(new Date());
		rdMmAccountLogDao.insert(inLog);
		//3.生成消息通知
		//3.1转账人消息通知
		ShopCommonMessage shopCommonMessage=new ShopCommonMessage();
		shopCommonMessage.setSendUid(rdMmAccountInfo.getMmCode());
		shopCommonMessage.setType(1);
		shopCommonMessage.setOnLine(1);
		shopCommonMessage.setCreateTime(new Date());
		shopCommonMessage.setBizType(2);
		shopCommonMessage.setIsTop(1);
		shopCommonMessage.setCreateTime(new Date());
		shopCommonMessage.setTitle("奖励积分转账通知");
		shopCommonMessage.setContent("已成功从奖励积分账户卡减"+total+"奖励积分转给会员"+acceptAccountInfo.getMmCode()+"，具体请查询奖励积分明细");
		Long msgId = twiterIdService.getTwiterId();
		shopCommonMessage.setId(msgId);
		shopCommonMessageDao.insert(shopCommonMessage);
		ShopMemberMessage shopMemberMessage=new ShopMemberMessage();
		shopMemberMessage.setBizType(2);
		shopMemberMessage.setCreateTime(new Date());
		shopMemberMessage.setId(twiterIdService.getTwiterId());
		shopMemberMessage.setIsRead(0);
		shopMemberMessage.setMsgId(msgId);
		shopMemberMessage.setUid(Long.parseLong(rdMmAccountInfo.getMmCode()));
		shopMemberMessageDao.insert(shopMemberMessage);
		//3.2收款人消息通知
		ShopCommonMessage shopCommonMessage1=new ShopCommonMessage();
		shopCommonMessage1.setSendUid(acceptAccountInfo.getMmCode());
		shopCommonMessage1.setType(1);
		shopCommonMessage1.setOnLine(1);
		shopCommonMessage1.setCreateTime(new Date());
		shopCommonMessage1.setBizType(2);
		shopCommonMessage1.setIsTop(1);
		shopCommonMessage1.setCreateTime(new Date());
		shopCommonMessage1.setTitle("奖励积分到账通知");
		shopCommonMessage1.setContent("您收到会员"+rdMmAccountInfo.getMmCode()+"转入的"+total+"奖励积分，具体请查询奖励积分明细");
		Long msgId1 = twiterIdService.getTwiterId();
		shopCommonMessage1.setId(msgId1);
		shopCommonMessageDao.insert(shopCommonMessage1);
		ShopMemberMessage shopMemberMessage1=new ShopMemberMessage();
		shopMemberMessage1.setBizType(2);
		shopMemberMessage1.setCreateTime(new Date());
		shopMemberMessage1.setId(twiterIdService.getTwiterId());
		shopMemberMessage1.setIsRead(0);
		shopMemberMessage1.setMsgId(msgId1);
		shopMemberMessage1.setUid(Long.parseLong(acceptAccountInfo.getMmCode()));
		shopMemberMessageDao.insert(shopMemberMessage1);
		map.put("batchNum",batchNum);
		map.put("amount",total.toString());
		map.put("balance",bonusAfter.toString());
		map.put("acceptNickName",inBasic.getMmNickName());
		map.put("acceptMobile",inBasic.getMobile());
		return map;
	}
}
