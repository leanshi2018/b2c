package com.framework.loippi.service;

import com.framework.loippi.entity.Log;

/**
 * Service - 日志
 * 
 * @author Mounate Yan。
 * @version 1.0
 */
public interface LogService extends GenericService<Log, Long> {

	/**
	 * 清空日志
	 */
	void clear();
}