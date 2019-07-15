package com.framework.loippi.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.GenSchemeDao;
import com.framework.loippi.entity.GenScheme;
import com.framework.loippi.service.GenSchemeService;

/**
 * Service - 生成方案
 * 
 * @author Mounate Yan。
 * @version 1.0
 */
@Service("genSchemeServiceImpl")
public class GenSchemeServiceImpl extends GenericServiceImpl<GenScheme, Long> implements GenSchemeService{

	@Autowired
	private GenSchemeDao genSchemeDao;
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(genSchemeDao);
	}
}