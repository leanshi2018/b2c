package com.framework.loippi.service.impl.order;

import com.framework.loippi.dao.order.ShopOrderLogisticsDao;
import com.framework.loippi.entity.order.ShopOrderLogistics;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.order.ShopOrderLogisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * SERVICE - ShopOrderLogistics(订单商品物流)
 * 
 * @author dzm
 * @version 2.0
 */
@Service
public class ShopOrderLogisticsServiceImpl extends GenericServiceImpl<ShopOrderLogistics, Long> implements ShopOrderLogisticsService {
	
	@Autowired
	private ShopOrderLogisticsDao shopOrderLogisticsDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopOrderLogisticsDao);
	}
}
