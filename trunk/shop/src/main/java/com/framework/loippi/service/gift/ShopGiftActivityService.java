package com.framework.loippi.service.gift;

import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.gift.ShopGiftActivity;
import com.framework.loippi.service.GenericService;

/**
 * @author :ldq
 * @date:2020/12/1
 * @description:dubbo com.framework.loippi.service.gift
 */
public interface ShopGiftActivityService extends GenericService<ShopGiftActivity, Long> {
	List<ShopGiftActivity> findByState(Integer estate);

	Map<String, String> saveOrEditGift(ShopGiftActivity shopGiftActivity, Long id, String username);

	ShopGiftActivity findById(Long id);

	void updateByEState(Integer estate);
}
