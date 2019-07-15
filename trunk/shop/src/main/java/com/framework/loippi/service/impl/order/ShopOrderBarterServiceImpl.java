package com.framework.loippi.service.impl.order;

import com.framework.loippi.service.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.order.ShopOrderBarterDao;
import com.framework.loippi.entity.order.ShopOrderBarter;
import com.framework.loippi.service.order.ShopOrderBarterService;

/**
 * SERVICE - ShopOrderBarter(换货表)
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopOrderBarterServiceImpl extends GenericServiceImpl<ShopOrderBarter, Long> implements ShopOrderBarterService {
	
	@Autowired
	private ShopOrderBarterDao shopOrderBarterDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopOrderBarterDao);
	}
}
