package com.framework.loippi.service.impl.product;

import com.framework.loippi.service.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.product.ShopGoodsTypeBrandDao;
import com.framework.loippi.entity.product.ShopGoodsTypeBrand;
import com.framework.loippi.service.product.ShopGoodsTypeBrandService;

import java.util.List;

/**
 * SERVICE - ShopGoodsTypeBrand(商品类型与品牌对应表)
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopGoodsTypeBrandServiceImpl extends GenericServiceImpl<ShopGoodsTypeBrand, Long> implements ShopGoodsTypeBrandService {
	
	@Autowired
	private ShopGoodsTypeBrandDao shopGoodsTypeBrandDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopGoodsTypeBrandDao);
	}

	@Override
	public void batchSave(List<ShopGoodsTypeBrand> list) {

	}

	@Override
	public List<ShopGoodsTypeBrand> findByTypes(Long typeID) {
		return shopGoodsTypeBrandDao.findByTypes(typeID);
	}
}
