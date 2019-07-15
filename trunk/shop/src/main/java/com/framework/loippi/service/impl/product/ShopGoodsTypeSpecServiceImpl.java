package com.framework.loippi.service.impl.product;

import com.framework.loippi.service.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.product.ShopGoodsTypeSpecDao;
import com.framework.loippi.entity.product.ShopGoodsTypeSpec;
import com.framework.loippi.service.product.ShopGoodsTypeSpecService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SERVICE - ShopGoodsTypeSpec(商品类型与规格对应表)
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopGoodsTypeSpecServiceImpl extends GenericServiceImpl<ShopGoodsTypeSpec, Long> implements ShopGoodsTypeSpecService {
	
	@Autowired
	private ShopGoodsTypeSpecDao shopGoodsTypeSpecDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopGoodsTypeSpecDao);
	}

	@Override
	public void batchSave(List<ShopGoodsTypeSpec> list) {

	}

	@Override
	public List<ShopGoodsTypeSpec> findByTypes(Long typeID) {
		return shopGoodsTypeSpecDao.findByTypes(typeID);
	}

	public void deleteByTypeIds(Long[] ids){
		if(ids==null||ids.length==0)return;
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("ids",ids);
		shopGoodsTypeSpecDao.deleteByTypeIds(map);
	}

	@Override
	public void deleteByMap(Map<String, Object> params) {
		shopGoodsTypeSpecDao.deleteByMap(params);
	}

}
