package com.framework.loippi.service.impl.walet;
import com.framework.loippi.dao.user.ShopMemberPaymentTallyDao;
import com.framework.loippi.dao.walet.ShopWalletLogDao;
import com.framework.loippi.entity.user.ShopMemberPaymentTally;
import com.framework.loippi.entity.walet.LgTypeEnum;
import com.framework.loippi.entity.walet.ShopWalletLog;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.utils.Dateutil;
import com.framework.loippi.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.walet.ShopWalletRechargeDao;
import com.framework.loippi.entity.walet.ShopWalletRecharge;
import com.framework.loippi.service.wallet.ShopWalletRechargeService;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SERVICE - ShopWalletRecharge(预存款充值表)
 * 
 * @author zijing
 * @version 2.0
 */
@Service
@Slf4j
public class ShopWalletRechargeServiceImpl extends GenericServiceImpl<ShopWalletRecharge, Long> implements ShopWalletRechargeService {
	
	@Autowired
	private ShopWalletRechargeDao shopWalletRechargeDao;

	//支付流水
	@Autowired
	private ShopMemberPaymentTallyDao paymentTallyDao;




	@Resource
	private ShopWalletLogDao shopWalletLogDao;

	@Resource
	private TwiterIdService  twiterIdService;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopWalletRechargeDao);
	}

	/**
	 * 充值订单支付后，回调处理充值的相关事宜：记录流水和更新账户余额
	 * @param paySn
	 */
	public void payRechargeOrderCallback(String paySn){

//		log.info("===========充值订单支付后，回调处理充值的相关事宜：记录流水和更新账户余额!==============");
//		log.info("==================================paySn:"+paySn+"==============================");
//		if(StringUtil.isEmpty(paySn)){
//			log.info("====================支付订单流水号为空！无法进行充值处理！================================");
//			return;
//		}
//		Map<String,Object> qyMap = new HashMap<>();
//		qyMap.put("paymentSn",paySn);
//		//查找支付流水信息
//		List<ShopMemberPaymentTally> paymentTallyList = paymentTallyDao.findByParams(qyMap);
//		if(paymentTallyList==null||paymentTallyList.size()==0){
//			log.info("====================支付订单流水号信息为空！无法进行充值处理！================================");
//			return;
//		}
//		if(paymentTallyList==null||paymentTallyList.size()>1){
//			log.info("====================支付订单流水号信息有多条！无法进行充值处理！================================");
//			return;
//		}
//		ShopMemberPaymentTally  paymentTally = paymentTallyList.get(0);
//		if(paymentTally==null){
//			log.info("====================支付订单流水号信息为空！无法进行充值处理！================================");
//			return;
//		}
//		if(paymentTally.getBuyerId()==null){
//			log.info("====================支付订单流水号信息无法找到充值会员id！无法进行充值处理！================================");
//			return;
//		}
//		//修改充值状态
//		qyMap.clear();
//		qyMap.put("pdrSn", paySn);
//		List<ShopWalletRecharge> shopWalletRechargeList = shopWalletRechargeDao.findByParams(qyMap);
//		ShopWalletRecharge shopWalletRecharge = null;
//		if(shopWalletRechargeList!=null&&shopWalletRechargeList.size()>0){
//			shopWalletRecharge = shopWalletRechargeList.get(0);
//		}
//		if(shopWalletRecharge==null){
//			log.info("====================支付订单流水号信息无法找到对应的充值记录！无法进行充值处理！================================");
//			return;
//		}
//		if(shopWalletRecharge.getPdrPaymentState().equals("1")){
//			log.info("====================充值已处理！避免回调多次导致余额错误！================================");
//			return;
//		}
//		//用户充值
//		ShopMember shopMember = shopMemberDao.find(paymentTally.getBuyerId());
//		BigDecimal amout =shopMember.getAvailablePredeposit().add(shopWalletRecharge.getPdrAmount());
//		log.info("支付金额："+paymentTally.getPaymentAmount().toString()+",充值金额："
//				+shopWalletRecharge.getPdrAmount().toString()+",充值后客户余额："+amout.toString());
//		shopMember.setAvailablePredeposit(amout);
//		shopMemberDao.update(shopMember);
//		if (shopWalletRecharge != null) {
//			shopWalletRecharge.setPdrPaymentState("1");
//			shopWalletRecharge.setPdrPaymentCode(paymentTally.getPaymentCode());
//			shopWalletRecharge.setPdrPaymentName(paymentTally.getPaymentName());
//			shopWalletRecharge.setPdrPaymentTime(new Date());
//			shopWalletRechargeDao.update(shopWalletRecharge);
//			System.out.print("充值成功");
//
//			//写入钱包流水
//			ShopWalletLog shopWalletLog = new ShopWalletLog();
//            shopWalletLog.setId(twiterIdService.getTwiterId());
//            shopWalletLog.setBizId(shopWalletRecharge.getId());
//            shopWalletLog.setOrderSn(shopWalletRecharge.getPdrSn());
//            shopWalletLog.setLgMemberId(shopWalletRecharge.getPdrMemberId());
//            shopWalletLog.setLgMemberName(shopWalletRecharge.getPdrMemberName());
//            shopWalletLog.setCreateTime(new Date());
//            shopWalletLog.setLgType(LgTypeEnum.RECHARGE.value);
//            shopWalletLog.setLgSn("W" + Dateutil.getDateString());
//            shopWalletLog.setLgAddAmount(shopWalletRecharge.getPdrAmount());
//            shopWalletLog.setLgAvAmount(shopMember.getAvailablePredeposit().add(shopWalletRecharge.getPdrAmount()));
//            shopWalletLog.setLgFreezeAmount(new BigDecimal(0));
//            shopWalletLog.setLgRdeAmount(new BigDecimal(0));
//            shopWalletLog.setLgDesc("用户充值金额" + shopWalletRecharge.getPdrAmount() + "元");
//			//shopWalletLog.setId(twiterIdService.getTwiterId());
//			//shopWalletLog.setLgSn("W" + Dateutil.getDateString());
//			//shopWalletLog.setBizId(shopWalletRecharge.getId());
//			//shopWalletLog.setBizSn(shopWalletRecharge.getPdrSn());
//			//shopWalletLog.setLgMemberId(shopMember.getId());
//			//shopWalletLog.setLgMemberName(shopMember.getMemberTruename());
//			//shopWalletLog.setCreateTime(new Date());
//			//shopWalletLog.setLgType("recharge");
//			//shopWalletLog.setLgSn("W" + Dateutil.getDateString());
//			//shopWalletLog.setLgAddAmount(shopWalletRecharge.getPdrAmount());
//			//shopWalletLog.setLgAvAmount(shopMember.getAvailablePredeposit());
//			//shopWalletLog.setLgFreezeAmount(new BigDecimal(0));
//			//shopWalletLog.setLgDesc("用户成功充值: " + shopWalletRecharge.getPdrAmount().setScale(2, BigDecimal.ROUND_HALF_UP).toString());
//			shopWalletLogDao.insert(shopWalletLog);
//		}
//		log.info("====================paySn:"+paySn+",充值支付回调处理完毕！================================");
	}
}
