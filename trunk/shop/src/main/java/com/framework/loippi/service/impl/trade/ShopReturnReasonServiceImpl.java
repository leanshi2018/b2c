package com.framework.loippi.service.impl.trade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.trade.ShopReturnReasonDao;
import com.framework.loippi.entity.trade.ShopReturnReason;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.trade.ShopReturnReasonService;

/**
 * SERVICE - ShopReturnReason()
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopReturnReasonServiceImpl extends GenericServiceImpl<ShopReturnReason, Long> implements ShopReturnReasonService {
	
	@Autowired
	private ShopReturnReasonDao shopReturnReasonDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopReturnReasonDao);
	}
}
