package com.framework.loippi.service.order;

import com.framework.loippi.entity.order.ShopOrderPay;
import com.framework.loippi.service.GenericService;

/**
 * SERVICE - ShopOrderPay(订单支付表)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopOrderPayService extends GenericService<ShopOrderPay, Long> {

    ShopOrderPay findBySn(String paySn);
}
