package com.framework.loippi.service.impl.product;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.product.ShopExpressSpecialGoodsDao;
import com.framework.loippi.entity.product.ShopExpressSpecialGoods;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.product.ShopExpressSpecialGoodsService;

/**
 * @author :ldq
 * @date:2019/10/25
 * @description:dubbo com.framework.loippi.service.impl.product
 */
@Service
public class ShopExpressSpecialGoodsServiceImpl extends GenericServiceImpl<ShopExpressSpecialGoods, Long> implements ShopExpressSpecialGoodsService {

	@Autowired
	private ShopExpressSpecialGoodsDao shopExpressSpecialGoodsDao;

	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopExpressSpecialGoodsDao);
	}

	@Override
	public List<ShopExpressSpecialGoods> findByState(Integer eState) {
		return shopExpressSpecialGoodsDao.findByState(eState);
	}
}
