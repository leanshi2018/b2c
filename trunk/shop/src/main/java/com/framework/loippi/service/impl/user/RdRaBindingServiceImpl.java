package com.framework.loippi.service.impl.user;

import com.framework.loippi.dao.user.RdRaBindingDao;
import com.framework.loippi.entity.user.RdRaBinding;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdRaBindingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * SERVICE - RdRaBinding(会员老系统绑定信息)
 * 
 * @author dzm
 * @version 2.0
 */
@Service
public class RdRaBindingServiceImpl extends GenericServiceImpl<RdRaBinding, Long> implements RdRaBindingService {
	
	@Autowired
	private RdRaBindingDao rdRaBindingDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdRaBindingDao);
	}
}
