package com.framework.loippi.service.impl.order;

import java.util.HashMap;
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
}
