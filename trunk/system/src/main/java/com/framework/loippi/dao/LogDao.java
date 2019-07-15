package com.framework.loippi.dao;

import com.framework.loippi.entity.Log;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * DAO - Log
 * 
 * @author Loippi Team
 * @version 1.0
 */
public interface LogDao  extends GenericDao<Log, Long> {
	
	/**
	 * 清除日志
	 */
	void clear();
}
