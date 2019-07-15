package com.framework.loippi.service.impl.user;

import com.framework.loippi.dao.user.RdGoodsAdjustmentDao;
import com.framework.loippi.entity.user.RdGoodsAdjustment;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdGoodsAdjustmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
}
