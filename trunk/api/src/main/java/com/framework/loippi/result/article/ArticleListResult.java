package com.framework.loippi.result.article;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.framework.loippi.entity.common.ShopCommonArticle;
import com.framework.loippi.entity.common.ShopCommonArticleClass;
import com.framework.loippi.utils.JacksonUtil;

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
        @JsonSerialize(using = ToStringSerializer.class)
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
         * 无格式文章内容
         */
        private String info;

        /**
         * 图片
         */
        private String image;

        /**
         * 图片
         */
        private ArrayList<String> imageList;
        /**
         * url
         */
        private String url;

        /**
         * 浏览量
         */
        private Integer pageViews;

        /**
         * 喜欢数
         */
        private Integer likeNum;

        /**
         * 跳转链接
         */
        private String articleUrl;

        /**
         * 跳转路径类型
         */
        private String pageType;

        /**
         * 跳转路径参数
         */
        private String pagePath;

        /**
         * 组合跳转路径类型及参数
         */
        private String pageData;

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
       @JsonSerialize(using = ToStringSerializer.class)
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
                 articleinfo.setInfo(item.getArticleInfo());
                 articleinfo.setCreateTime(item.getCreateTime());
                 articleinfo.setId(item.getId());
                 if(item.getArticleImage()==null||item.getArticleImage().length()==0){
                     articleinfo.setImage("");
                     articleinfo.setImageList(new ArrayList<String>());
                 }else {
                     ArrayList<String> strings = new ArrayList<>();
                     String[] split = item.getArticleImage().split(",");
                     articleinfo.setImage(split[0]);
                     for (String s : split) {
                         strings.add(s);
                     }
                     articleinfo.setImageList(strings);
                 }
                 articleinfo.setPageViews(Optional.ofNullable(item.getPageViews()).orElse(0));
                 articleinfo.setLikeNum(Optional.ofNullable(item.getLikeNum()).orElse(0));
                 articleinfo.setArticleUrl(Optional.ofNullable(item.getArticleUrl()).orElse(""));
                 articleinfo.setPageType(Optional.ofNullable(item.getPageType()).orElse(""));
                 articleinfo.setPagePath(Optional.ofNullable(item.getPagePath()).orElse(""));
                 Map<String,Object> map = new HashMap<String,Object>();
                 if (item.getPageType()!=null){
                     map.put("page",item.getPageType());
                 }else {
                     map.put("page","");
                 }

                 if (item.getPagePath()!=null){
                     Map<String, String> jsonMap = JacksonUtil.readJsonToMap(item.getPagePath());
                     Set<String> strings = jsonMap.keySet();
                     Iterator<String> iterator = strings.iterator();
                     while (iterator.hasNext()){
                         String key = iterator.next();
                         String value = jsonMap.get(key);
                         map.put(key,value);
                     }
                 }

                 JSONObject activityUrlJson = JSONObject.fromObject(map);
                 articleinfo.setPageData(activityUrlJson.toString());
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
                articleinfo.setInfo(item.getArticleInfo());
                articleinfo.setCreateTime(item.getCreateTime());
                articleinfo.setId(item.getId());
                if(item.getArticleImage()==null||item.getArticleImage().length()==0){
                    articleinfo.setImage("");
                    articleinfo.setImageList(new ArrayList<String>());
                }else {
                    ArrayList<String> strings = new ArrayList<>();
                    String[] split = item.getArticleImage().split(",");
                    for (String s : split) {
                        strings.add(s);
                    }
                    articleinfo.setImage(split[0]);
                    articleinfo.setImageList(strings);
                }
                articleinfo.setPageViews(Optional.ofNullable(item.getPageViews()).orElse(0));
                articleinfo.setLikeNum(Optional.ofNullable(item.getLikeNum()).orElse(0));
                articleinfo.setArticleUrl(Optional.ofNullable(item.getArticleUrl()).orElse(""));
                articleinfo.setPageType(Optional.ofNullable(item.getPageType()).orElse(""));
                articleinfo.setPagePath(Optional.ofNullable(item.getPagePath()).orElse(""));
                Map<String,Object> map = new HashMap<String,Object>();
                if (item.getPageType()!=null){
                    map.put("page",item.getPageType());
                }else {
                    map.put("page","");
                }

                if (item.getPagePath()!=null){
                    Map<String, String> jsonMap = JacksonUtil.readJsonToMap(item.getPagePath());
                    Set<String> strings = jsonMap.keySet();
                    Iterator<String> iterator = strings.iterator();
                    while (iterator.hasNext()){
                        String key = iterator.next();
                        String value = jsonMap.get(key);
                        map.put(key,value);
                    }
                }

                JSONObject activityUrlJson = JSONObject.fromObject(map);
                articleinfo.setPageData(activityUrlJson.toString());
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
