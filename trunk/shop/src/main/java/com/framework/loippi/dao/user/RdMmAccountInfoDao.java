package com.framework.loippi.dao.user;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.user.RdMmAccountInfo;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * DAO - RdMmAccountInfo(会员账户信息)
 * 
 * @author zijing
 * @version 2.0
 */
public interface RdMmAccountInfoDao  extends GenericDao<RdMmAccountInfo, Long> {

	void updateAddBonusBlance(Map<String, Object> map);

	List<RdMmAccountInfo> findByMCode(String mmCode);

	RdMmAccountInfo findAccByMCode(String sponsorCode);

	List<RdMmAccountInfo> findLastWithdrawalOneHundred(BigDecimal acc);
}
