package com.framework.loippi.service.order;

import java.math.BigDecimal;

import com.framework.loippi.entity.order.ShopOrderSplit;
import com.framework.loippi.service.GenericService;

/**
 * SERVICE - ShopOrderSplit(订单分单表)
 *
 * @author zc
 * @date 2020/11/05
 */
public interface ShopOrderSplitService extends GenericService<ShopOrderSplit, Long> {

	BigDecimal findSplitPpv(String mmCode, String period);
}
