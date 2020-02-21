package com.framework.loippi.service.impl.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.product.ShopGoodsStintDao;
import com.framework.loippi.entity.product.ShopGoodsStint;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.product.ShopGoodsStintService;

/**
 * @author :ldq
 * @date:2020/2/20
 * @description:dubbo com.framework.loippi.service.impl.product
 */
@Service
public class ShopGoodsStintServiceImpl extends GenericServiceImpl<ShopGoodsStint, Long> implements ShopGoodsStintService {

	@Autowired
	private ShopGoodsStintDao shopGoodsStintDao;

	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopGoodsStintDao);
	}

}
