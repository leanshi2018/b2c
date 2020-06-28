package com.framework.loippi.service.impl.ware;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.ware.RdWareAdjustDao;
import com.framework.loippi.entity.ware.RdWareAdjust;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.ware.RdWareAdjustService;


/**
 * SERVICE - RdWareAdjust(发货单表)
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class RdWareAdjustServiceImpl extends GenericServiceImpl<RdWareAdjust, Integer> implements RdWareAdjustService {
	
	@Autowired
	private RdWareAdjustDao rdWareAdjustDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdWareAdjustDao);
	}

	@Override
	public void insert(RdWareAdjust rdWareAdjust) {
		rdWareAdjustDao.insert(rdWareAdjust);
	}
}
