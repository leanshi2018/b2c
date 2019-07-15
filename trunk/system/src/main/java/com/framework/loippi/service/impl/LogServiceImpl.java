package com.framework.loippi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.LogDao;
import com.framework.loippi.entity.Log;
import com.framework.loippi.service.LogService;

/**
 * Service - 日志
 * 
 * @author Mounate Yan。
 * @version 1.0
 */
@Service("logServiceImpl")
public class LogServiceImpl extends GenericServiceImpl<Log, Long> implements LogService {

	@Autowired
	private LogDao logDao;
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(logDao);
	}
	public void clear() {
		logDao.clear();
	}
}