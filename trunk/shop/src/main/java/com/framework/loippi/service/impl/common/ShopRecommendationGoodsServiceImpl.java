package com.framework.loippi.service.impl.common;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.framework.loippi.dao.common.ShopRecommendationGoodsDao;
import com.framework.loippi.entity.common.ShopRecommendationGoods;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.result.common.coupon.CouponUserLogResult;
import com.framework.loippi.result.common.recommendation.RecommendationGoodsResult;
import com.framework.loippi.service.common.ShopRecommendationGoodsService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;

/**
 * @author :ldq
 * @date:2020/8/21
 * @description:dubbo com.framework.loippi.service.impl.common
 */
@Service
@Slf4j
public class ShopRecommendationGoodsServiceImpl extends GenericServiceImpl<ShopRecommendationGoods, Long> implements ShopRecommendationGoodsService {

	@Resource
	private ShopRecommendationGoodsDao shopRecommendationGoodsDao;

	@Override
	public void delByRId(Long rId) {
		shopRecommendationGoodsDao.delByRId(rId);
	}

	@Override
	public Page findGoodsResult(Pageable pageable) {
		PageList<RecommendationGoodsResult> result = shopRecommendationGoodsDao.findGoodsResult(pageable.getParameter(), pageable.getPageBounds());
		return new Page<>(result, result.getPaginator().getTotalCount(), pageable);
	}
}
