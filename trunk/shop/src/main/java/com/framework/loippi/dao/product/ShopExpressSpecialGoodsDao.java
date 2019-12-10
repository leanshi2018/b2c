package com.framework.loippi.dao.product;

import java.util.List;

import com.framework.loippi.entity.product.ShopExpressSpecialGoods;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * @author :ldq
 * @date:2019/10/25
 * @description:dubbo com.framework.loippi.dao.product
 */
public interface ShopExpressSpecialGoodsDao extends GenericDao<ShopExpressSpecialGoods, Long> {
	List<ShopExpressSpecialGoods> findByState(Integer eState);

	List<ShopExpressSpecialGoods> findByExpressId(Long expressId);
}
