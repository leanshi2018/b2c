package com.framework.loippi.service.impl.order;

import com.framework.loippi.service.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.order.ShopOrderBarterLogDao;
import com.framework.loippi.entity.order.ShopOrderBarterLog;
import com.framework.loippi.service.order.ShopOrderBarterLogService;

/**
 * SERVICE - ShopOrderBarterLog(换货处理日志)
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopOrderBarterLogServiceImpl extends GenericServiceImpl<ShopOrderBarterLog, Long> implements ShopOrderBarterLogService {
	
	@Autowired
	private ShopOrderBarterLogDao shopOrderBarterLogDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopOrderBarterLogDao);
	}
}
