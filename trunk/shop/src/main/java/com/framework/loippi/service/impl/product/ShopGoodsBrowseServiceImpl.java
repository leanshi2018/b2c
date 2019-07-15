package com.framework.loippi.service.impl.product;

import com.framework.loippi.dao.product.ShopGoodsBrowseDao;
import com.framework.loippi.entity.product.ShopGoodsBrowse;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.product.ShopGoodsBrowseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * SERVICE - ShopReturnReason()
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopGoodsBrowseServiceImpl extends GenericServiceImpl<ShopGoodsBrowse, Long> implements ShopGoodsBrowseService {
	
	@Autowired
	private ShopGoodsBrowseDao shopGoodsBrowseDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopGoodsBrowseDao);
	}

	@Override
	public Long deleteMemberFavorites(Map<String, Object> var1) {
		return shopGoodsBrowseDao.deleteMemberFavorites(var1);
	}
}
