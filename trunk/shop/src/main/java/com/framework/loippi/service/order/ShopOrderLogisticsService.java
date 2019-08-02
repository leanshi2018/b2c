package com.framework.loippi.service.order;


import com.framework.loippi.entity.order.ShopOrderLogistics;
import com.framework.loippi.service.GenericService;

/**
 * SERVICE - ShopOrderLogistics(订单商品物流)
 * 
 * @author dzm
 * @version 2.0
 */
public interface ShopOrderLogisticsService  extends GenericService<ShopOrderLogistics, Long> {

	void insert(ShopOrderLogistics shopOrderLogistics);
}
