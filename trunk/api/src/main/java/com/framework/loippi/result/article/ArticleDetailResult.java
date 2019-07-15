package com.framework.loippi.result.article;

import com.framework.loippi.entity.common.ShopCommonArticle;
import com.framework.loippi.entity.common.ShopCommonArticleClass;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by neil on 2017/7/13.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDetailResult {

    private Long id;
    private Integer pageViews;
    private String articleTitle;
    private String articleContent;

    public static ArticleDetailResult buildList(ShopCommonArticle shopCommonArticle) {
        ArticleDetailResult articleDetailResult=new ArticleDetailResult();
        if (shopCommonArticle!=null){
            articleDetailResult.setId(shopCommonArticle.getId());
            articleDetailResult.setArticleContent(shopCommonArticle.getArticleContent());
            articleDetailResult.setArticleTitle(shopCommonArticle.getArticleTitle());
            articleDetailResult.setPageViews(shopCommonArticle.getPageViews());
        }
        return articleDetailResult;
    }
}
