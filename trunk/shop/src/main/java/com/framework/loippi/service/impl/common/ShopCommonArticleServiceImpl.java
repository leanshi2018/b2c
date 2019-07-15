package com.framework.loippi.service.impl.common;

import com.framework.loippi.service.impl.GenericServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.common.ShopCommonArticleDao;
import com.framework.loippi.entity.common.ShopCommonArticle;
import com.framework.loippi.service.common.ShopCommonArticleService;

/**
 * SERVICE - ShopCommonArticle(文章表)
 *
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopCommonArticleServiceImpl extends GenericServiceImpl<ShopCommonArticle, Long> implements ShopCommonArticleService {

    @Autowired
    private ShopCommonArticleDao shopCommonArticleDao;


    @Autowired
    public void setGenericDao() {
        super.setGenericDao(shopCommonArticleDao);
    }

    /**
     * 批量修改发布状态
     *
     * @param ids
     * @param status
     * @return
     */
    public boolean updateStatusBatch(Long[] ids, Integer status) {
        if (ids == null || ids.length == 0 || status == null) return false;
        try {
            for (Long id : ids) {
                ShopCommonArticle article = new ShopCommonArticle();
                article.setId(id);
                article.setStatus(status);
                shopCommonArticleDao.update(article);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
