package com.framework.loippi.service.impl.ware;

import com.framework.loippi.dao.ware.RdWarehouseDao;
import com.framework.loippi.entity.ware.RdWarehouse;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.ware.RdWarehouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * SERVICE - RdWarehouse(仓库记录表)
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class RdWarehouseServiceImpl extends GenericServiceImpl<RdWarehouse, Long> implements RdWarehouseService {
	
	@Autowired
	private RdWarehouseDao rdWarehouseDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdWarehouseDao);
	}
}
