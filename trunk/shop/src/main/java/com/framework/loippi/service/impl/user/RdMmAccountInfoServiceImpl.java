package com.framework.loippi.service.impl.user;



import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import com.framework.loippi.dao.user.RdMmAccountInfoDao;
import com.framework.loippi.dao.user.RdMmAccountLogDao;
import com.framework.loippi.dao.user.RdSysPeriodDao;
import com.framework.loippi.entity.ShopCommonMessage;
import com.framework.loippi.entity.ShopMemberMessage;
import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdMmAccountInfoService;

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
}
