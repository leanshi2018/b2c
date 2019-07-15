package com.framework.loippi.service.impl.user;

import com.framework.loippi.dao.user.RdSysPeriodDao;
import com.framework.loippi.entity.user.RdSysPeriod;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdSysPeriodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;


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
}
