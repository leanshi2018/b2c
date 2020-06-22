package com.framework.loippi.service.impl.ware;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.ware.RdWareOrderGoodsDao;
import com.framework.loippi.entity.ware.RdWareOrderGoods;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.ware.RdWareOrderGoodsService;

/**
 * @author :ldq
 * @date:2020/6/19
 * @description:dubbo com.framework.loippi.service.impl.ware
 */
@Service
public class RdWareOrderGoodsServiceImpl extends GenericServiceImpl<RdWareOrderGoods, Long> implements RdWareOrderGoodsService {
	@Autowired
	private RdWareOrderGoodsDao rdWareOrderGoodsDao;


	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdWareOrderGoodsDao);
	}
}
