package com.framework.loippi.service.product;

import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.product.ShopGoodsGoods;
import com.framework.loippi.service.GenericService;

/**
 * SERVICE - ShopGoodsGoods(组合商品，商品选择)
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopGoodsGoodsService  extends GenericService<ShopGoodsGoods, Long> {

	List<ShopGoodsGoods> findGoodsGoodsByGoodsId(Long goodsId1);

	ShopGoodsGoods findGoodsGoods(Map<String, Object> map1);
}
