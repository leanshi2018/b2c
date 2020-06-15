package com.framework.loippi.service.common;

import com.framework.loippi.entity.common.ShopCommonArticleClass;
import com.framework.loippi.service.GenericService;

import java.util.HashMap;
import java.util.List;

/**
 * SERVICE - ShopCommonArticleClass(文章分类表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopCommonArticleClassService  extends GenericService<ShopCommonArticleClass, Long> {

    List<ShopCommonArticleClass> findByAcSort(HashMap<String, Object> map);
}
