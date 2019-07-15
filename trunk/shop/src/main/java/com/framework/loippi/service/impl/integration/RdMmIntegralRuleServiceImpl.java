package com.framework.loippi.service.impl.integration;

import com.framework.loippi.dao.integration.RdMmIntegralRuleDao;
import com.framework.loippi.entity.integration.RdMmIntegralRule;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.integration.RdMmIntegralRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * SERVICE - RdMmIntegralRule(积分规则设置表)
 * 
 * @author dzm
 * @version 2.0
 */
@Service
public class RdMmIntegralRuleServiceImpl extends GenericServiceImpl<RdMmIntegralRule, Long> implements RdMmIntegralRuleService {
	
	@Autowired
	private RdMmIntegralRuleDao rdMmIntegralRuleDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdMmIntegralRuleDao);
	}
}
