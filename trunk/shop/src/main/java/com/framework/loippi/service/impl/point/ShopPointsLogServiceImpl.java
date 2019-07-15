package com.framework.loippi.service.impl.point;

import com.framework.loippi.service.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.point.ShopPointsLogDao;
import com.framework.loippi.entity.point.ShopPointsLog;
import com.framework.loippi.service.point.ShopPointsLogService;

/**
 * SERVICE - ShopPointsLog(会员积分日志表)
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopPointsLogServiceImpl extends GenericServiceImpl<ShopPointsLog, Long> implements ShopPointsLogService {
	
	@Autowired
	private ShopPointsLogDao shopPointsLogDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopPointsLogDao);
	}
}
