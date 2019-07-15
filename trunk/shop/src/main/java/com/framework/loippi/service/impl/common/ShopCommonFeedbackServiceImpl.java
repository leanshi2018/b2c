package com.framework.loippi.service.impl.common;

import com.framework.loippi.service.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.common.ShopCommonFeedbackDao;
import com.framework.loippi.entity.common.ShopCommonFeedback;
import com.framework.loippi.service.common.ShopCommonFeedbackService;

/**
 * SERVICE - ShopCommonFeedback(系统反馈)
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopCommonFeedbackServiceImpl extends GenericServiceImpl<ShopCommonFeedback, Long> implements ShopCommonFeedbackService {
	
	@Autowired
	private ShopCommonFeedbackDao shopCommonFeedbackDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopCommonFeedbackDao);
	}
}
