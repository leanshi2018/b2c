package com.framework.loippi.service.impl.product;

import com.framework.loippi.dao.product.ShopGoodsEvaluateKeywordsDao;
import com.framework.loippi.entity.product.ShopGoodsEvaluateKeywords;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.product.ShopGoodsEvaluateKeywordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * SERVICE - ShopGoodsEvaluateKeywords(评价关键字表)
 * 
 * @author dzm
 * @version 2.0
 */
@Service
public class ShopGoodsEvaluateKeywordsServiceImpl extends GenericServiceImpl<ShopGoodsEvaluateKeywords, Long> implements ShopGoodsEvaluateKeywordsService {
	
	@Autowired
	private ShopGoodsEvaluateKeywordsDao shopGoodsEvaluateKeywordsDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopGoodsEvaluateKeywordsDao);
	}
}
