package com.framework.loippi.service.gift;

import java.util.List;

import com.framework.loippi.entity.gift.ShopGiftGoods;
import com.framework.loippi.service.GenericService;

/**
 * @author :ldq
 * @date:2020/12/1
 * @description:dubbo com.framework.loippi.service.gift
 */
public interface ShopGiftGoodsService extends GenericService<ShopGiftGoods, Long> {
	List<ShopGiftGoods> findByGiftIdAndWRule(Long giftId, Integer wRule);
}
