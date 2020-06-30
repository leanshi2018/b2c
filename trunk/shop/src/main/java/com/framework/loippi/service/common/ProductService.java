package com.framework.loippi.service.common;

import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsSpec;

public interface ProductService {

	void updateStorage(ShopGoodsSpec goodsSpec, ShopGoods goods);

	void updateStorageNew(ShopGoodsSpec goodsSpec, ShopGoods goods, Integer logisticType);
}
