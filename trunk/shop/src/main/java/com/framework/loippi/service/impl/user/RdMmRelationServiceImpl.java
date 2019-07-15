package com.framework.loippi.service.impl.user;

import com.framework.loippi.dao.user.RdMmRelationDao;
import com.framework.loippi.entity.user.RdMmRelation;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdMmRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * SERVICE - RdMmRelation(会员关系状态表)
 * 
 * @author dzm
 * @version 2.0
 */
@Service
public class RdMmRelationServiceImpl extends GenericServiceImpl<RdMmRelation, Long> implements RdMmRelationService {
	
	@Autowired
	private RdMmRelationDao rdMmRelationDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdMmRelationDao);
	}
}
