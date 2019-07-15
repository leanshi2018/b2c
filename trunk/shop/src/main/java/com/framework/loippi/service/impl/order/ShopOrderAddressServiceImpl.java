package com.framework.loippi.service.impl.order;

import com.framework.loippi.service.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.order.ShopOrderAddressDao;
import com.framework.loippi.entity.order.ShopOrderAddress;
import com.framework.loippi.service.order.ShopOrderAddressService;

/**
 * SERVICE - ShopOrderAddress(订单地址信息表)
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopOrderAddressServiceImpl extends GenericServiceImpl<ShopOrderAddress, Long> implements ShopOrderAddressService {
	
	@Autowired
	private ShopOrderAddressDao shopOrderAddressDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopOrderAddressDao);
	}
}
