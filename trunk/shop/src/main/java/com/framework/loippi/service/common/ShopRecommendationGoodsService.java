package com.framework.loippi.service.common;

import com.framework.loippi.entity.common.ShopRecommendationGoods;
import com.framework.loippi.service.GenericService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;

/**
 * @author :ldq
 * @date:2020/8/21
 * @description:dubbo com.framework.loippi.service.common
 */
public interface ShopRecommendationGoodsService extends GenericService<ShopRecommendationGoods, Long> {
	void delByRId(Long rId);

	Page findGoodsResult(Pageable pageable);

}
