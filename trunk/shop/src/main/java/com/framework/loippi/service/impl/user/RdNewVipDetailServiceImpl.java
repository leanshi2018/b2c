package com.framework.loippi.service.impl.user;

import com.framework.loippi.dao.user.RdNewVipDetailDao;
import com.framework.loippi.entity.user.RdNewVipDetail;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdNewVipDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * SERVICE - RdNewVipDetail(会员推荐表)
 * 
 * @author dzm
 * @version 2.0
 */
@Service
public class RdNewVipDetailServiceImpl extends GenericServiceImpl<RdNewVipDetail, Long> implements RdNewVipDetailService {
	
	@Autowired
	private RdNewVipDetailDao rdNewVipDetailDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdNewVipDetailDao);
	}
}
