package com.framework.loippi.service.order;

import java.util.List;

import com.framework.loippi.entity.order.ShopSpiritOrderInfo;
import com.framework.loippi.service.GenericService;

/**
 * @author :ldq
 * @date:2020/9/18
 * @description:dubbo com.framework.loippi.service.order
 */
public interface ShopSpiritOrderInfoService extends GenericService<ShopSpiritOrderInfo, Long> {
	ShopSpiritOrderInfo findByOrderIdAndSpecId(Long orderId, Long specId);

	List<ShopSpiritOrderInfo> findNoSubmitOrderAll();

	void updateTrackSnByOrderId(Long orderId, String trackSn);

	void updateSubmitStateAndMsgByOrderId(Integer submitState, String msg, Long orderId);

	List<ShopSpiritOrderInfo> findByOrderId(Long orderId);
}
