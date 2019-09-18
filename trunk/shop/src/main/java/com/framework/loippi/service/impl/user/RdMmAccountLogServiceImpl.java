package com.framework.loippi.service.impl.user;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.user.RdMmAccountLogDao;
import com.framework.loippi.dao.user.RdSysPeriodDao;
import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdMmAccountLogService;



/**
 * SERVICE - RdMmAccountLog(会员账户交易日志表)
 * 
 * @author dzm
 * @version 2.0
 */
@Service
public class RdMmAccountLogServiceImpl extends GenericServiceImpl<RdMmAccountLog, Long> implements RdMmAccountLogService {
	
	@Autowired
	private RdMmAccountLogDao rdMmAccountLogDao;
	@Autowired
	private RdSysPeriodDao rdSysPeriodDao;

	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdMmAccountLogDao);
	}

	@Override
	public RdMmAccountLog findByTransNumber(Integer transNumber) {
		return rdMmAccountLogDao.findByTransNumber(transNumber);
	}

	@Override
	public int updateCancellWD(Integer transNumber) {
		String period = rdSysPeriodDao.getSysPeriodService(new Date());
		Map<String,Object> map = new HashMap<>();
		map.put("transNumber",transNumber);
		map.put("status",-2);
		map.put("accStatus",1);
		map.put("transPeriod",period);
		map.put("autohrizeDesc","用户本人取消");
		map.put("autohrizeTime",new Date());
		return rdMmAccountLogDao.updateCancellWD(map);
	}
}
