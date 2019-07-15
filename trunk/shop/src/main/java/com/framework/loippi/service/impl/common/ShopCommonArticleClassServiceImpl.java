package com.framework.loippi.service.impl.common;

import com.framework.loippi.service.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.common.ShopCommonArticleClassDao;
import com.framework.loippi.entity.common.ShopCommonArticleClass;
import com.framework.loippi.service.common.ShopCommonArticleClassService;

/**
 * SERVICE - ShopCommonArticleClass(文章分类表)
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopCommonArticleClassServiceImpl extends GenericServiceImpl<ShopCommonArticleClass, Long> implements ShopCommonArticleClassService {
	
	@Autowired
	private ShopCommonArticleClassDao shopCommonArticleClassDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(shopCommonArticleClassDao);
	}
}
