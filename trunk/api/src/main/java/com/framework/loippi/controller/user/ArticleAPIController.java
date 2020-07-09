package com.framework.loippi.controller.user;


import com.framework.loippi.consts.DocumentConsts;
import com.framework.loippi.controller.BaseController;


import com.framework.loippi.entity.common.ShopCommonArticle;
import com.framework.loippi.entity.common.ShopCommonArticleClass;
import com.framework.loippi.entity.walet.ShopWalletLog;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.result.article.ArticleDetailResult;
import com.framework.loippi.result.article.ArticleListResult;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.result.user.UserAddrsListResult;
import com.framework.loippi.service.common.ShopCommonArticleClassService;
import com.framework.loippi.service.common.ShopCommonArticleService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.Xerror;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;


/**
 * 学堂(文章)
 * Created by Administrator on 2017/11/23.
 */
@Controller
@ResponseBody
@RequestMapping("/api/article")
public class ArticleAPIController extends BaseController {

    @Resource
    ShopCommonArticleClassService shopCommonArticleClassService;

    @Resource
    ShopCommonArticleService shopCommonArticleService;
    @Value("#{properties['wap.server']}")
    private String wapServer;

    //列表
    @RequestMapping(value = "/list.json")
    public String articleList(@RequestParam(defaultValue = "1") Integer pageNumber,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              HttpServletRequest request) {
        //查找文章所有的分类
        List<ShopCommonArticleClass> shopCommonArticleClassList = shopCommonArticleClassService.findList(Paramap.create().put("acCode", DocumentConsts.DOCUMENT_TYPE_SCHOOL_ARTICLES)
                .put("acStatus", 0).put("order","ac_sort desc"));
        //查找文章
        Pageable pageable = new Pageable();
        pageable.setPageSize(pageSize);
        pageable.setPageNumber(pageNumber);
        ArrayList<Long> longs = new ArrayList<>();
        longs.add(6548852933067280384L);
        longs.add(6678225691906936832L);
        longs.add(6678225763373682688L);
        longs.add(6678225827387150336L);
        pageable.setParameter(Paramap.create().put("articleShow",1).put("articleIds",longs));//TODO 朋友圈素材以及下级分类不参与列表分类展示
        pageable.setOrderProperty("is_top,article_sort,page_views");
        pageable.setOrderDirection(Order.Direction.DESC);
        List<ShopCommonArticle> shopCommonArticleList = shopCommonArticleService.findByPage(pageable).getContent();
        return ApiUtils.success(ArticleListResult.buildList(shopCommonArticleList, shopCommonArticleClassList, 1,wapServer));
    }

    //详情
    @RequestMapping(value = "/detail")
    public String articleDetail(Long id, HttpServletRequest request) {
        if (id == null) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        ShopCommonArticle shopCommonArticle = shopCommonArticleService.find(id);
        //浏览量加1
        if (shopCommonArticle != null) {

            if (shopCommonArticle.getPageViews() == null) {
                shopCommonArticle.setPageViews(1);
            } else {
                shopCommonArticle.setPageViews(shopCommonArticle.getPageViews() + 1);
            }
            shopCommonArticleService.update(shopCommonArticle);
        }
        return ApiUtils.success(ArticleDetailResult.buildList(shopCommonArticle));
    }

    //搜索
    @RequestMapping(value = "/search.json")
    public String articleDetail(String key, HttpServletRequest request) {
        //查找文章所有的分类
        List<ShopCommonArticleClass> shopCommonArticleClassList = shopCommonArticleClassService.findList(Paramap.create().put("acCode", DocumentConsts.DOCUMENT_TYPE_SCHOOL_ARTICLES).put("acStatus", 0));
        List<ShopCommonArticle> shopCommonArticleList = shopCommonArticleService.findList(Paramap.create().put("key", key).put("articleShow",1));
        return ApiUtils.success(ArticleListResult.buildList(shopCommonArticleList, shopCommonArticleClassList, 2,wapServer));
    }

    //二级列表
    @RequestMapping(value = "/secondarylist.json")
    public String secondarylist(@RequestParam(defaultValue = "1") Integer pageNumber,
                                @RequestParam(defaultValue = "10") Integer pageSize,
                                HttpServletRequest request, Long firstClassificationId, Long secondaryClassificationId) {
        if (firstClassificationId == null) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        //查找改分类下文章所有二级的分类
        /*List<ShopCommonArticleClass> shopCommonArticleClassList = shopCommonArticleClassService.findList(Paramap.create().put("acCode", DocumentConsts.DOCUMENT_TYPE_SCHOOL_ARTICLES).put("acStatus", 0)
                .put("acParentId", firstClassificationId));*/
        HashMap<String, Object> map = new HashMap<>();
        map.put("acCode",DocumentConsts.DOCUMENT_TYPE_SCHOOL_ARTICLES);
        map.put("acStatus", 0);
        map.put("acParentId", firstClassificationId);
        List<ShopCommonArticleClass> shopCommonArticleClassList =shopCommonArticleClassService.findByAcSort(map);
        ShopCommonArticleClass shopCommonArticleClass = shopCommonArticleClassService.find(firstClassificationId);
        if (shopCommonArticleClassList == null || shopCommonArticleClassList.size() <= 0) {
            return ApiUtils.error("该分类不存在二级分类");
        }
        //查找文章
        Pageable pageable = new Pageable();
        pageable.setPageSize(pageSize);
        pageable.setPageNumber(pageNumber);
        pageable.setOrderProperty("is_top,article_sort,page_views");
        pageable.setOrderDirection(Order.Direction.DESC);
        if(firstClassificationId.equals(6548852933067280384L)){
            pageable.setOrderProperty("is_top,create_time,article_sort,page_views");
        }
        if (secondaryClassificationId != null) {
            pageable.setParameter(Paramap.create().put("acId", secondaryClassificationId).put("articleShow",1));
        } else {
            pageable.setParameter(Paramap.create().put("acId", shopCommonArticleClassList.get(0).getId()).put("articleShow",1));
        }
        List<ShopCommonArticle> shopCommonArticleList = shopCommonArticleService.findByPage(pageable).getContent();
        return ApiUtils.success(ArticleListResult.buildList2(shopCommonArticleList, shopCommonArticleClassList, shopCommonArticleClass,wapServer));
    }

    /**
     *
     * @param request
     * @param id 文章id
     * @param type 类型 0：收藏    1：取消收藏
     * @return
     */
    @RequestMapping(value = "/addLike.json")
    public String addLike(HttpServletRequest request,Long id,Integer type) {
        if(id==null){
            return ApiUtils.error("请选择需要收藏的学堂文章");
        }
        ShopCommonArticle article = shopCommonArticleService.find(id);
        if(article==null){
            return ApiUtils.error("学堂文章不存在");
        }
        Integer num = Optional.ofNullable(article.getLikeNum()).orElse(0);
        if(type==null||type!=1){
            article.setLikeNum(num+1);
        }else {
            if(num>0){
                article.setLikeNum(num-1);
            }
        }
        shopCommonArticleService.update(article);
        return ApiUtils.success();
    }
}
