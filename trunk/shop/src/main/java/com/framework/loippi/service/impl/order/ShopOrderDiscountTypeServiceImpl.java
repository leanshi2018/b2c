package com.framework.loippi.service.impl.order;

import com.framework.loippi.dao.order.ShopOrderDiscountTypeDao;
import com.framework.loippi.entity.order.ShopOrderDiscountType;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.order.ShopOrderDiscountTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;




/**
 * SERVICE - ShopOrderDiscountType(优惠订单类型)
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopOrderDiscountTypeServiceImpl extends GenericServiceImpl<ShopOrderDiscountType, Long> implements ShopOrderDiscountTypeService {
	
	@Autowired
	private ShopOrderDiscountTypeDao shopOrderDiscountTypeDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopOrderDiscountTypeDao);
	}
}
