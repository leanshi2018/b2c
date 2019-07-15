package com.framework.loippi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.GenTableColumnDao;
import com.framework.loippi.entity.GenTableColumn;
import com.framework.loippi.service.GenTableColumnService;

/**
 * Service - 表字段
 * 
 * @author Mounate Yan。
 * @version 1.0
 */
@Service("genTableColumnServiceImpl")
public class GenTableColumnServiceImpl extends GenericServiceImpl<GenTableColumn, Long> implements GenTableColumnService{

	@Autowired
	private GenTableColumnDao genTableColumnDao;
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(genTableColumnDao);
	}

	@Override
	public Long deleteByTableId(Long tableId) {
		return genTableColumnDao.deleteByTableId(tableId);
	}

}