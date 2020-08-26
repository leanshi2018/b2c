package com.framework.loippi.dao.common;

import com.framework.loippi.entity.common.ShopRecommendationGoods;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.mybatis.paginator.domain.PageBounds;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.result.common.recommendation.RecommendationGoodsResult;

/**
 * @author :ldq
 * @date:2020/8/21
 * @description:dubbo com.framework.loippi.dao.common
 */
public interface ShopRecommendationGoodsDao extends GenericDao<ShopRecommendationGoods, Long> {
	void delByRId(Long rId);

	PageList<RecommendationGoodsResult> findGoodsResult(Object parameter, PageBounds pageBounds);
}
