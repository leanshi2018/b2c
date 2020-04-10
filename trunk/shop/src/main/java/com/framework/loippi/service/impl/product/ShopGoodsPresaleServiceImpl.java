package com.framework.loippi.service.impl.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.product.ShopGoodsPresaleDao;
import com.framework.loippi.entity.product.ShopGoodsPresale;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.product.ShopGoodsPresaleService;

/**
 * @author :ldq
 * @date:2020/4/10
 * @description:dubbo com.framework.loippi.service.impl.product
 */
@Service
public class ShopGoodsPresaleServiceImpl extends GenericServiceImpl<ShopGoodsPresale, Long> implements ShopGoodsPresaleService {

	@Autowired
	private ShopGoodsPresaleDao shopGoodsPresaleDao;
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopGoodsPresaleDao);
	}
}
