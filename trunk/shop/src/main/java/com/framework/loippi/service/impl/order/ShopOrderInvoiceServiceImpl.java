package com.framework.loippi.service.impl.order;

import com.framework.loippi.service.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.order.ShopOrderInvoiceDao;
import com.framework.loippi.entity.order.ShopOrderInvoice;
import com.framework.loippi.service.order.ShopOrderInvoiceService;

/**
 * SERVICE - ShopOrderInvoice(买家发票信息表)
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopOrderInvoiceServiceImpl extends GenericServiceImpl<ShopOrderInvoice, Long> implements ShopOrderInvoiceService {
	
	@Autowired
	private ShopOrderInvoiceDao shopOrderInvoiceDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopOrderInvoiceDao);
	}
}
