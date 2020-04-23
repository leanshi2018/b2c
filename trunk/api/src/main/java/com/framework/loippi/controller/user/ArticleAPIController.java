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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
        pageable.setParameter(Paramap.create().put("articleShow",1));
        pageable.setOrderProperty("is_top,page_views");
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
        List<ShopCommonArticle> shopCommonArticleList = shopCommonArticleService.findList(Paramap.create().put("key", key));
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
        List<ShopCommonArticleClass> shopCommonArticleClassList = shopCommonArticleClassService.findList(Paramap.create().put("acCode", DocumentConsts.DOCUMENT_TYPE_SCHOOL_ARTICLES).put("acStatus", 0)
                .put("acParentId", firstClassificationId));
        ShopCommonArticleClass shopCommonArticleClass = shopCommonArticleClassService.find(firstClassificationId);
        if (shopCommonArticleClassList == null || shopCommonArticleClassList.size() <= 0) {
            return ApiUtils.error("该分类不存在二级分类");
        }
        //查找文章
        Pageable pageable = new Pageable();
        pageable.setPageSize(pageSize);
        pageable.setPageNumber(pageNumber);
        pageable.setOrderProperty("is_top,page_views");
        pageable.setOrderDirection(Order.Direction.DESC);
        if (secondaryClassificationId != null) {
            pageable.setParameter(Paramap.create().put("acId", secondaryClassificationId));
        } else {
            pageable.setParameter(Paramap.create().put("acId", shopCommonArticleClassList.get(0).getId()));
        }
        List<ShopCommonArticle> shopCommonArticleList = shopCommonArticleService.findByPage(pageable).getContent();
        return ApiUtils.success(ArticleListResult.buildList2(shopCommonArticleList, shopCommonArticleClassList, shopCommonArticleClass,wapServer));
    }
}
