package com.framework.loippi.service.impl.gift;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.dao.gift.ShopGiftGoodsDao;
import com.framework.loippi.entity.gift.ShopGiftGoods;
import com.framework.loippi.service.gift.ShopGiftGoodsService;
import com.framework.loippi.service.impl.GenericServiceImpl;

/**
 * @author :ldq
 * @date:2020/12/1
 * @description:dubbo com.framework.loippi.service.impl.gift
 */
@Service
@Transactional
public class ShopGiftGoodsServiceImpl extends GenericServiceImpl<ShopGiftGoods, Long> implements ShopGiftGoodsService {
	@Resource
	private ShopGiftGoodsDao shopGiftGoodsDao;

	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopGiftGoodsDao);
	}

	@Override
	public List<ShopGiftGoods> findByGiftIdAndWRule(Long giftId, Integer wRule) {
		Map<String,Object> map = new HashMap<>();
		map.put("giftId",giftId);
		map.put("wRule",wRule);
		return shopGiftGoodsDao.findByGiftIdAndWRule(map);
	}
}
