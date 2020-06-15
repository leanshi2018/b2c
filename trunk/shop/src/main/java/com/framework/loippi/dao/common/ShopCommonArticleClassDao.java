package com.framework.loippi.dao.common;

import com.framework.loippi.entity.common.ShopCommonArticleClass;
import com.framework.loippi.mybatis.dao.GenericDao;

import java.util.HashMap;
import java.util.List;

/**
 * DAO - ShopCommonArticleClass(文章分类表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopCommonArticleClassDao  extends GenericDao<ShopCommonArticleClass, Long> {

    List<ShopCommonArticleClass> findByAcSort(HashMap<String, Object> map);
}
