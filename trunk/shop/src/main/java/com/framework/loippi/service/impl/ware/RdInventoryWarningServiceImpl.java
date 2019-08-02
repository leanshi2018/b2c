package com.framework.loippi.service.impl.ware;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.ware.RdInventoryWarningDao;
import com.framework.loippi.entity.ware.RdInventoryWarning;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.ware.RdInventoryWarningService;



/**
 * SERVICE - RdInventoryWarning(仓库库存表)
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class RdInventoryWarningServiceImpl extends GenericServiceImpl<RdInventoryWarning, Long> implements RdInventoryWarningService {
	
	@Autowired
	private RdInventoryWarningDao rdInventoryWarningDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdInventoryWarningDao);
	}

	@Override
	public void updateInventoryByWareCodeAndSpecId(String wareCode, Long goodsSpecId, Integer quantity) {
		Map<String,Object> map = new HashMap<>();
		map.put("wareCode",wareCode);
		map.put("specificationId",goodsSpecId);
		map.put("inventory",quantity);
		rdInventoryWarningDao.updateInventoryByWareCodeAndSpecId(map);
	}
}
