package com.framework.loippi.service.impl.user;

import com.framework.loippi.dao.user.RdMmAccountLogDao;
import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdMmAccountLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



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
	public void setGenericDao() {
		super.setGenericDao(rdMmAccountLogDao);
	}
}
