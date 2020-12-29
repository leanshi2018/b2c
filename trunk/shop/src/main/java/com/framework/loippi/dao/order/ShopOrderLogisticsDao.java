package com.framework.loippi.dao.order;


import java.util.Map;

import com.framework.loippi.entity.order.ShopOrderLogistics;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * DAO - ShopOrderLogistics(订单商品物流)
 * 
 * @author dzm
 * @version 2.0
 */
public interface ShopOrderLogisticsDao  extends GenericDao<ShopOrderLogistics, Long> {

	void updateOrderShipping(Map<String, Object> map);
}
