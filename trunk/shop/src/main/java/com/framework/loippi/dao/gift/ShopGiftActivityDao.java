package com.framework.loippi.dao.gift;

import java.util.List;

import com.framework.loippi.entity.gift.ShopGiftActivity;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * @author :ldq
 * @date:2020/12/1
 * @description:dubbo com.framework.loippi.dao.gift
 */
public interface ShopGiftActivityDao extends GenericDao<ShopGiftActivity, Long> {
	List<ShopGiftActivity> findByState(Integer eState);

	void updateByEState(Integer eState);
}
