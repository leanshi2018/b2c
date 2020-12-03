package com.framework.loippi.dao.gift;

import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.gift.ShopGiftGoods;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * @author :ldq
 * @date:2020/12/1
 * @description:dubbo com.framework.loippi.dao.gift
 */
public interface ShopGiftGoodsDao extends GenericDao<ShopGiftGoods, Long> {
	List<ShopGiftGoods> findByGiftIdAndWRule(Map<String, Object> map);

	void deleteByGiftId(Long id);
}
