package com.framework.loippi.service.impl;

import com.framework.loippi.dao.GenTableDao;
import com.framework.loippi.entity.GenTable;
import com.framework.loippi.service.GenTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service - 表
 * 
 * @author Mounate Yan。
 * @version 1.0
 */
@Service("genTableServiceImpl")
public class GenTableServiceImpl extends GenericServiceImpl<GenTable, Long> implements GenTableService{

	@Autowired
	private GenTableDao genTableDao;
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(genTableDao);
	}

	@Override
	public List<GenTable> findAllTable(String tableName) {
		return genTableDao.findAllTable(tableName);
	}
}