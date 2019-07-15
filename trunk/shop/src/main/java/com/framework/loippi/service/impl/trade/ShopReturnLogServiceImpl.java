package com.framework.loippi.service.impl.trade;

import com.framework.loippi.dao.trade.ShopReturnLogDao;
import com.framework.loippi.entity.trade.ShopReturnLog;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.trade.ShopReturnLogService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * SERVICE - ShopReturnLog()
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopReturnLogServiceImpl extends GenericServiceImpl<ShopReturnLog, Long> implements ShopReturnLogService {
	
	@Autowired
	private ShopReturnLogDao shopReturnLogDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopReturnLogDao);
	}
}
