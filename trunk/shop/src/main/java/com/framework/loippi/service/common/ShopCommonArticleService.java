package com.framework.loippi.service.common;

import com.framework.loippi.entity.common.ShopCommonArticle;
import com.framework.loippi.service.GenericService;

/**
 * SERVICE - ShopCommonArticle(文章表)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopCommonArticleService extends GenericService<ShopCommonArticle, Long> {

    boolean updateStatusBatch(Long[] ids, Integer status);

}
