package com.framework.loippi.service.impl.order;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.order.ShopOrderLogisticsDao;
import com.framework.loippi.entity.common.ShopCommonExpress;
import com.framework.loippi.entity.order.ShopOrderLogistics;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.order.ShopOrderLogisticsService;


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

	@Override
	public void insert(ShopOrderLogistics shopOrderLogistics) {
		shopOrderLogisticsDao.insert(shopOrderLogistics);
	}

	@Override
	public void updateOrderShipping(Long orderId, String trackSn, ShopCommonExpress express) {
		Map<String, Object> map = new HashMap<>();
		map.put("orderId", orderId);
		map.put("shippingCode", trackSn);
		map.put("shippingExpressCode", Optional.ofNullable(express.getECode()).orElse(""));
		map.put("shippingExpressId", Optional.ofNullable(express.getId()).orElse(-1L));
		shopOrderLogisticsDao.updateOrderShipping(map);
	}
}
