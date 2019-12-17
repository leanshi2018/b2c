package com.framework.loippi.service.product;

import java.util.List;

import com.framework.loippi.entity.common.ShopCommonExpress;
import com.framework.loippi.entity.product.ShopExpressSpecialGoods;
import com.framework.loippi.service.GenericService;
import com.framework.loippi.support.Pageable;

/**
 * @author :ldq
 * @date:2019/10/25
 * @description:dubbo com.framework.loippi.service.product
 */
public interface ShopExpressSpecialGoodsService extends GenericService<ShopExpressSpecialGoods, Long> {
	List<ShopExpressSpecialGoods> findByState(Integer state);

	List<ShopExpressSpecialGoods> findByExpressId(Long id);

	void addExpressSpecialGoods(ShopCommonExpress express, Long[] specIds,String adminName);

	Object findListResultByPage(Pageable pageable);
}
