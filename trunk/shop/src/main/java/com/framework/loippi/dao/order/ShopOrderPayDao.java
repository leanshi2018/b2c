package com.framework.loippi.dao.order;

import com.framework.loippi.entity.order.ShopOrderPay;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * DAO - ShopOrderPay(订单支付表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopOrderPayDao  extends GenericDao<ShopOrderPay, Long> {

    void updateByPaysn(ShopOrderPay orderPay);

}
