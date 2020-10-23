package com.framework.loippi.dao.product;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.framework.loippi.entity.product.ShopGoodsGoods;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * DAO - ShopGoodsGoods(组合商品，商品选择)
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopGoodsGoodsDao  extends GenericDao<ShopGoodsGoods, Long> {

    void deleteNotIds(@Param("ids") List<Long> ids);

	List<ShopGoodsGoods> findGoodsGoodsByGoodsId(Long goodsId);

	ShopGoodsGoods findGoodsGoods(Map<String, Object> map);

	List<ShopGoodsGoods> findGoodsGoodsList(Map<String, Object> map);

}
