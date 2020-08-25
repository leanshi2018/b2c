package com.framework.loippi.service.impl.common;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.framework.loippi.dao.common.ShopProductRecommendationDao;
import com.framework.loippi.entity.common.ShopProductRecommendation;
import com.framework.loippi.service.common.ShopProductRecommendationService;
import com.framework.loippi.service.impl.GenericServiceImpl;

/**
 * @author :ldq
 * @date:2020/8/21
 * @description:dubbo com.framework.loippi.service.impl.common
 */
@Service
@Slf4j
public class ShopProductRecommendationServiceImpl extends GenericServiceImpl<ShopProductRecommendation, Long> implements ShopProductRecommendationService {

	@Resource
	private ShopProductRecommendationDao shopProductRecommendationDao;
}
