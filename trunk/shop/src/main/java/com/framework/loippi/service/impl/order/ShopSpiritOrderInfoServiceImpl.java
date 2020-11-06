package com.framework.loippi.service.impl.order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.order.ShopSpiritOrderInfoDao;
import com.framework.loippi.entity.order.ShopSpiritOrderInfo;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.order.ShopSpiritOrderInfoService;

/**
 * @author :ldq
 * @date:2020/9/18
 * @description:dubbo com.framework.loippi.service.impl.order
 */
@Service
public class ShopSpiritOrderInfoServiceImpl extends GenericServiceImpl<ShopSpiritOrderInfo, Long>
		implements ShopSpiritOrderInfoService {

	@Autowired
	private ShopSpiritOrderInfoDao shopSpiritOrderInfoDao;

	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopSpiritOrderInfoDao);
	}

	@Override
	public ShopSpiritOrderInfo findByOrderIdAndSpecId(Long orderId, Long specId) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("orderId",orderId);
		map.put("specId",specId);
 		return shopSpiritOrderInfoDao.findByOrderIdAndSpecId(map);
	}

	@Override
	public List<ShopSpiritOrderInfo> findNoSubmitOrderAll() {
		return shopSpiritOrderInfoDao.findNoSubmitOrderAll();
	}

	@Override
	public void updateTrackSnByOrderId(Long orderId, String trackSn) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("orderId",orderId);
		map.put("trackSn",trackSn);
		shopSpiritOrderInfoDao.updateTrackSnByOrderId(map);
	}

	@Override
	public void updateSubmitStateAndMsgByOrderId(Integer submitState, String msg, Long orderId) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("orderId",orderId);
		map.put("msg",msg);
		map.put("submitState",submitState);
		shopSpiritOrderInfoDao.updateSubmitStateAndMsgByOrderId(map);
	}

	@Override
	public List<ShopSpiritOrderInfo> findByOrderId(Long orderId) {
		return shopSpiritOrderInfoDao.findByOrderId(orderId);
	}
}
