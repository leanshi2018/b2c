package com.framework.loippi.service.impl.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.user.RdMmEditDao;
import com.framework.loippi.entity.user.RdMmEdit;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdMmEditService;


/**
 * SERVICE - RdMmEdit(会员修改记录信息)
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class RdMmEditServiceImpl extends GenericServiceImpl<RdMmEdit, Long> implements RdMmEditService {
	
	@Autowired
	private RdMmEditDao rdMmEditDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdMmEditDao);
	}

	@Override
	public void updateByStatusAndMCode(String mmCode) {
		rdMmEditDao.updateByStatusAndMCode(mmCode);
	}
}
