package com.framework.loippi.service.impl.user;

import com.framework.loippi.dao.user.RdRanksDao;
import com.framework.loippi.entity.user.RdRanks;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdRanksService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * SERVICE - RdRanks(会员等级)
 * 
 * @author dzm
 * @version 2.0
 */
@Service
public class RdRanksServiceImpl extends GenericServiceImpl<RdRanks, Long> implements RdRanksService {
	
	@Autowired
	private RdRanksDao rdRanksDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdRanksDao);
	}
}
