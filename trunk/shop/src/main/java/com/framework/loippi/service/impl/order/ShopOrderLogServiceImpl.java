package com.framework.loippi.service.impl.order;

import com.framework.loippi.service.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.order.ShopOrderLogDao;
import com.framework.loippi.entity.order.ShopOrderLog;
import com.framework.loippi.service.order.ShopOrderLogService;

/**
 * SERVICE - ShopOrderLog(订单处理历史表)
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopOrderLogServiceImpl extends GenericServiceImpl<ShopOrderLog, Long> implements ShopOrderLogService {
	
	@Autowired
	private ShopOrderLogDao shopOrderLogDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopOrderLogDao);
	}
}
