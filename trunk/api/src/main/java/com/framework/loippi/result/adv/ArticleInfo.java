package com.framework.loippi.result.adv;

import com.framework.loippi.entity.common.ShopCommonArticle;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by longbh on 2018/11/25.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleInfo {

    private Long id;
    private String articleUrl;
    private String articleTitle;
    private String articleContent;

    public static ArticleInfo of(ShopCommonArticle shopCommonArticle) {
        ArticleInfo articleInfo = new ArticleInfo();
        articleInfo.setId(shopCommonArticle.getId());
        articleInfo.setArticleUrl(shopCommonArticle.getArticleUrl());
        articleInfo.setArticleTitle(shopCommonArticle.getArticleTitle());
        articleInfo.setArticleContent(shopCommonArticle.getArticleContent());
        return articleInfo;
    }

    public static List<ArticleInfo> forList(List<ShopCommonArticle> shopCommonArticles) {
        List<ArticleInfo> articleInfos = new ArrayList<>();
        for (ShopCommonArticle shopCommonArticle : shopCommonArticles) {
            articleInfos.add(of(shopCommonArticle));
        }
        return articleInfos;
    }

}
