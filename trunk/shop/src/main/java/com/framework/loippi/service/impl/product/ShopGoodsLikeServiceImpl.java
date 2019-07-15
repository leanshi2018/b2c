package com.framework.loippi.service.impl.product;

import com.framework.loippi.service.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.product.ShopGoodsLikeDao;
import com.framework.loippi.entity.product.ShopGoodsLike;
import com.framework.loippi.service.product.ShopGoodsLikeService;

/**
 * SERVICE - ShopGoodsLike(商品评价点赞表)
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopGoodsLikeServiceImpl extends GenericServiceImpl<ShopGoodsLike, Long> implements ShopGoodsLikeService {
	
	@Autowired
	private ShopGoodsLikeDao shopGoodsLikeDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopGoodsLikeDao);
	}

	@Override
	public ShopGoodsLike findById(String evalGoodsId, Long id) {
		return null;
	}
}
