package com.framework.loippi.service.impl.ware;

import com.framework.loippi.dao.ware.RdInventoryWarningDao;
import com.framework.loippi.entity.ware.RdInventoryWarning;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.ware.RdInventoryWarningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



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
}
