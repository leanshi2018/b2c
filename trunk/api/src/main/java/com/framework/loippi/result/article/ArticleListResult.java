package com.framework.loippi.result.article;

import com.framework.loippi.entity.common.ShopCommonArticle;
import com.framework.loippi.entity.common.ShopCommonArticleClass;
import com.framework.loippi.entity.common.ShopCommonFeedback;
import com.framework.loippi.support.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by neil on 2017/7/13.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleListResult {

    /** 所有一级分类 */
    List<ArticleClass> articleClassList;
    /** 所有文章信息列表 */
    List<Articleinfo> articleinfoList;
    @Data
    static  class Articleinfo {
        /**  */
        private Long id;

        /**
         * 标题
         */
        private String title;

        /**
         * 创建时间
         */
        private java.util.Date createTime;

        /**
         * 内容
         */
        private String content;

        /**
         * 图片
         */
        private String image;
        /**
         * url
         */
        private String url;

        /**
         * 浏览量
         */
        private Integer pageViews;

        /**
         * 一级分类
         */
        private String firstClassificationName;

        /**
         * 二级分类
         */
        private String secondaryClassificationName;
    }
    @Data
    static  class ArticleClass{
       Long  articleClassId;
       String articleClassName;
    }

    public static ArticleListResult buildList(List<ShopCommonArticle> shopCommonArticleList,List<ShopCommonArticleClass> shopCommonArticleClassList,Integer type,String wapServer) {
        ArticleListResult articleListResult=new ArticleListResult();
        Map<String,String> Articleinfo=new HashMap<>();
        Map<String,String> ArticleClassinfo=new HashMap<>();
        List<ArticleClass> articleClassList=new ArrayList<>();
        List<Articleinfo> articleinfoList=new ArrayList<>();
         if (shopCommonArticleClassList!=null && shopCommonArticleClassList.size()>0){
             for (ShopCommonArticleClass item:shopCommonArticleClassList) {
                 if (item.getAcParentId()==0 && type==1){
                     ArticleClass articleClass=new ArticleClass();
                     articleClass.setArticleClassId(item.getId());
                     articleClass.setArticleClassName(item.getAcName());
                     articleClassList.add(articleClass);
                 }
                 Articleinfo.put(item.getId()+"",item.getAcParentId()+"");
                 ArticleClassinfo.put(item.getId()+"",item.getAcName());
             }
         }
         if (shopCommonArticleList!=null && shopCommonArticleList.size()>0){
             for (ShopCommonArticle item:shopCommonArticleList) {
                 Articleinfo articleinfo=new Articleinfo();
                    if (!"".equals(Articleinfo.get(item.getAcId()+"")) && Articleinfo.get(item.getAcId()+"")!=null){
                        String acParentId=Articleinfo.get(item.getAcId()+"");
                        articleinfo.setFirstClassificationName(Optional.ofNullable(ArticleClassinfo.get(acParentId)).orElse(""));
                        articleinfo.setSecondaryClassificationName( Optional.ofNullable(ArticleClassinfo.get(item.getAcId()+"")).orElse(""));
                    }else{
                        articleinfo.setFirstClassificationName(Optional.ofNullable(ArticleClassinfo.get(item.getAcId())).orElse(""));
                        articleinfo.setSecondaryClassificationName("");
                    }

                 articleinfo.setContent(item.getArticleContent());
                 articleinfo.setCreateTime(item.getCreateTime());
                 articleinfo.setId(item.getId());
                 articleinfo.setImage(Optional.ofNullable(item.getArticleImage()).orElse(""));
                 articleinfo.setPageViews(Optional.ofNullable(item.getPageViews()).orElse(0));
                 articleinfo.setTitle(item.getArticleTitle());
                 // TODO: 2018/12/5  待h5补充url
                 StringBuffer shareUrl = new StringBuffer();
                 shareUrl.append(wapServer);
                 shareUrl.append("/wap/article/");
                 shareUrl.append(item.getId());
                 shareUrl.append(".html");
                 articleinfo.setUrl(shareUrl.toString());
                 articleinfoList.add(articleinfo);

             }
         }
        articleListResult.setArticleClassList(articleClassList);
        articleListResult.setArticleinfoList(articleinfoList);
        return articleListResult;

    }
    public static ArticleListResult buildList2(List<ShopCommonArticle> shopCommonArticleList,List<ShopCommonArticleClass> shopCommonArticleClassList,ShopCommonArticleClass shopCommonArticleClass,String wapServer) {
        ArticleListResult articleListResult=new ArticleListResult();
        Map<String,String> ArticleClassinfo=new HashMap<>();
        List<ArticleClass> articleClassList=new ArrayList<>();
        List<Articleinfo> articleinfoList=new ArrayList<>();
        if (shopCommonArticleClassList!=null && shopCommonArticleClassList.size()>0){
            for (ShopCommonArticleClass item:shopCommonArticleClassList) {
                if (item.getAcParentId()!=0){
                    ArticleClass articleClass=new ArticleClass();
                    articleClass.setArticleClassId(item.getId());
                    articleClass.setArticleClassName(item.getAcName());
                    articleClassList.add(articleClass);
                }
                ArticleClassinfo.put(item.getId()+"",item.getAcName());
            }
        }
        if (shopCommonArticleList!=null && shopCommonArticleList.size()>0){
            for (ShopCommonArticle item:shopCommonArticleList) {
                Articleinfo articleinfo=new Articleinfo();
                if (ArticleClassinfo.get(item.getAcId()+"")!=null && ArticleClassinfo.get(item.getAcId()+"")!=""){

                 articleinfo.setFirstClassificationName(Optional.ofNullable(shopCommonArticleClass.getAcName()).orElse(""));
                 articleinfo.setSecondaryClassificationName( Optional.ofNullable(ArticleClassinfo.get(item.getAcId()+"")).orElse(""));

                articleinfo.setContent(item.getArticleContent());
                articleinfo.setCreateTime(item.getCreateTime());
                articleinfo.setId(item.getId());
                articleinfo.setImage(Optional.ofNullable(item.getArticleImage()).orElse(""));
                articleinfo.setPageViews(Optional.ofNullable(item.getPageViews()).orElse(0));
                articleinfo.setTitle(item.getArticleTitle());
                // TODO: 2018/12/5  待h5补充url
                    // TODO: 2018/12/5  待h5补充url
                    StringBuffer shareUrl = new StringBuffer();
                    shareUrl.append(wapServer);
                    shareUrl.append("/wap/article/");
                    shareUrl.append(item.getId());
                    shareUrl.append(".html");
                    articleinfo.setUrl(shareUrl.toString());
                }
                articleinfoList.add(articleinfo);

            }
        }
        articleListResult.setArticleClassList(articleClassList);
        articleListResult.setArticleinfoList(articleinfoList);
        return articleListResult;

    }
}
