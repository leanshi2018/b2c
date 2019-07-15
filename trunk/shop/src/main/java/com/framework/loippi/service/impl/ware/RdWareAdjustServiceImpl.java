package com.framework.loippi.service.impl.ware;

import com.framework.loippi.dao.ware.RdWareAdjustDao;
import com.framework.loippi.entity.ware.RdWareAdjust;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.ware.RdWareAdjustService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * SERVICE - RdWareAdjust(发货单表)
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class RdWareAdjustServiceImpl extends GenericServiceImpl<RdWareAdjust, Long> implements RdWareAdjustService {
	
	@Autowired
	private RdWareAdjustDao rdWareAdjustDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdWareAdjustDao);
	}
}
