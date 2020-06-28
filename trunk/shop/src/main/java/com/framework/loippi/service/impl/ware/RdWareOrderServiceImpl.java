package com.framework.loippi.service.impl.ware;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.ware.RdWareOrderDao;
import com.framework.loippi.entity.ware.RdWareOrder;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.ware.RdWareOrderService;

/**
 * @author :ldq
 * @date:2020/6/18
 * @description:dubbo com.framework.loippi.service.impl.ware
 */
@Service
public class RdWareOrderServiceImpl extends GenericServiceImpl<RdWareOrder, Long> implements RdWareOrderService {
	@Autowired
	private RdWareOrderDao rdWareOrderDao;


	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdWareOrderDao);
	}

	@Override
	public RdWareOrder findBySn(String orderSn) {
		return rdWareOrderDao.findBySn(orderSn);
	}
}
