package com.framework.loippi.service.impl.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.product.ShopBundledGoodsDao;
import com.framework.loippi.entity.product.ShopBundledGoods;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.product.ShopBundledGoodsService;

/**
 * @author :ldq
 * @date:2021/1/15
 * @description:dubbo com.framework.loippi.service.impl.product
 */
@Service
public class ShopBundledGoodsServiceImpl extends GenericServiceImpl<ShopBundledGoods, Long> implements ShopBundledGoodsService {

	@Autowired
	private ShopBundledGoodsDao shopBundledGoodsDao;

	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopBundledGoodsDao);
	}
}
