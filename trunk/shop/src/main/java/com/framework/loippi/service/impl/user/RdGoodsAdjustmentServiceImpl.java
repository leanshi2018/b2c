package com.framework.loippi.service.impl.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.user.RdGoodsAdjustmentDao;
import com.framework.loippi.entity.user.RdGoodsAdjustment;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdGoodsAdjustmentService;


/**
 * SERVICE - RdGoodsAdjustment(入库记录表)
 * 
 * @author dzm
 * @version 2.0
 */
@Service
public class RdGoodsAdjustmentServiceImpl extends GenericServiceImpl<RdGoodsAdjustment, Long> implements RdGoodsAdjustmentService {
	
	@Autowired
	private RdGoodsAdjustmentDao rdGoodsAdjustmentDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdGoodsAdjustmentDao);
	}

	@Override
	public void insert(RdGoodsAdjustment rdGoodsAdjustment) {
		rdGoodsAdjustmentDao.insert(rdGoodsAdjustment);
	}

	@Override
	public List<RdGoodsAdjustment> findByWidAndSign(int wId, int sign) {
		Map<String,Object> map = new HashMap<>();
		map.put("wid",wId);
		map.put("sign",sign);
		return rdGoodsAdjustmentDao.findByWidAndSign(map);
	}
}
