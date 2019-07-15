package com.framework.loippi.service.impl.trade;

import com.framework.loippi.dao.trade.ShopReturnOrderGoodsDao;
import com.framework.loippi.entity.trade.ShopReturnOrderGoods;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.trade.ShopReturnOrderGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * SERVICE - ShopReturnOrderGoods(售后订单商品表)
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopReturnOrderGoodsServiceImpl extends GenericServiceImpl<ShopReturnOrderGoods, Long> implements ShopReturnOrderGoodsService {
	
	@Autowired
	private ShopReturnOrderGoodsDao shopReturnOrderGoodsDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopReturnOrderGoodsDao);
	}
}
