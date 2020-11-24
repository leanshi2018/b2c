package com.framework.loippi.dao.order;

import java.math.BigDecimal;
import java.util.Map;

import com.framework.loippi.entity.order.ShopOrderSplit;
import com.framework.loippi.mybatis.dao.GenericDao;

public interface ShopOrderSplitDao extends GenericDao<ShopOrderSplit, Long> {
	BigDecimal findSplitPpv(Map<String, Object> map);
}
