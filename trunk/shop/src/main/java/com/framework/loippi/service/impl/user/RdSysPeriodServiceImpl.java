package com.framework.loippi.service.impl.user;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.user.RdSysPeriodDao;
import com.framework.loippi.entity.user.RdSysPeriod;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdSysPeriodService;


/**
 * SERVICE - RdSysPeriod(业务周期)
 * 
 * @author dzm
 * @version 2.0
 */
@Service
public class RdSysPeriodServiceImpl extends GenericServiceImpl<RdSysPeriod, Long> implements RdSysPeriodService {
	
	@Autowired
	private RdSysPeriodDao rdSysPeriodDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdSysPeriodDao);
	}

	@Override
	public String getSysPeriodService(Date date) {
		return rdSysPeriodDao.getSysPeriodService(date);
	}

	@Override
	public RdSysPeriod getPeriodService(Date date) {
		return rdSysPeriodDao.getPeriodService(date);
	}
}
