package com.framework.loippi.controller.wap;

import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.activity.ShopActivity;
import com.framework.loippi.entity.activity.ShopActivityGoods;
import com.framework.loippi.entity.activity.ShopActivityGoodsSpec;
import com.framework.loippi.entity.common.ShopApp;
import com.framework.loippi.entity.common.ShopCommonArticle;
import com.framework.loippi.entity.common.ShopCommonDocument;
import com.framework.loippi.entity.product.*;
import com.framework.loippi.result.activity.ActivityDetailResult;
import com.framework.loippi.result.common.activity.ActivityEvaluateGoodsResult;
import com.framework.loippi.result.common.activity.ActivityGoodsDetailResult;
import com.framework.loippi.result.common.activity.ActivityGoodsSkuInfo;
import com.framework.loippi.result.common.activity.ActivityGoodsSpesDescItem;
import com.framework.loippi.service.activity.ShopActivityGoodsService;
import com.framework.loippi.service.activity.ShopActivityGoodsSpecService;
import com.framework.loippi.service.activity.ShopActivityService;
import com.framework.loippi.service.common.ShopAppService;
import com.framework.loippi.service.common.ShopCommonArticleService;
import com.framework.loippi.service.common.ShopCommonDocumentService;
import com.framework.loippi.service.product.*;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.GoodsUtils;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.vo.goods.GoodsSpecVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Created by longbh on 2018/12/27.
 */
@Controller("wapGoodsDetailController")
public class WapShareController extends BaseController {

    @Resource
    private ShopActivityGoodsService shopActivityGoodsService;
    @Resource
    private ShopActivityGoodsSpecService shopActivityGoodsSpecService;
    @Resource
    private ShopGoodsService shopGoodsService;
    @Resource
    private ShopGoodsSpecService shopGoodsSpecService;
    @Resource
    private ShopGoodsEvaluateService shopGoodsEvaluateService;
    @Resource
    private ShopActivityService shopActivityService;
    @Resource
    private ShopGoodsBrandService shopGoodsBrandService;
    @Resource
    private ShopGoodsRecommendService shopGoodsRecommendService;
    @Resource
    private ShopCommonDocumentService shopCommonDocumentService;
    @Resource
    private ShopCommonArticleService shopCommonArticleService;
    @Resource
    private ShopAppService shopAppService;
    /**
     * 系统文章
     *
     * @param model
     * @param request
     * @param docType
     * @return
     */
    @RequestMapping(value = "/wap/document/{docType}", method = RequestMethod.GET)
    public String document(ModelMap model, HttpServletRequest request, @PathVariable("docType") String docType) {
        ShopCommonDocument document = shopCommonDocumentService.find("docType",docType);
        if(document == null){
            return "";
        }
        model.addAttribute("date", document.getCreateTime());
        model.addAttribute("title", document.getDocTitle());
        model.addAttribute("content", document.getDocContent());
        model.addAttribute("viewPoint",document.getPageViews() == null ? 1 : (document.getPageViews() + 1));
        //叠加访问量
        document.setPageViews(document.getPageViews() == null ? 1 : (document.getPageViews() + 1));
        shopCommonDocumentService.update(document);
        return "wap/article/detail";
    }


    @RequestMapping(value = "/wap/document/detail/{id}", method = RequestMethod.GET)
    public String document(ModelMap model, HttpServletRequest request, @PathVariable("id") Long id) {
        ShopCommonDocument document = shopCommonDocumentService.find(id);
        if(document == null){
            return "";
        }
        model.addAttribute("date", document.getCreateTime());
        model.addAttribute("title", document.getDocTitle());
        model.addAttribute("content", document.getDocContent());
        model.addAttribute("viewPoint",document.getPageViews() == null ? 1 : (document.getPageViews() + 1));
        //叠加访问量
        document.setPageViews(document.getPageViews() == null ? 1 : (document.getPageViews() + 1));
        shopCommonDocumentService.update(document);
        return "wap/article/detail";
    }

    /**
     * 文章
     *
     * @param model
     * @param request
     * @param article
     * @return
     */
    @RequestMapping(value = "/wap/article/{id}", method = RequestMethod.GET)
    public String article(ModelMap model, HttpServletRequest request, @PathVariable("id") Long article) {
        ShopCommonArticle shopCommonArticle = shopCommonArticleService.find(article);
        model.addAttribute("date", shopCommonArticle.getArticleTime());
        model.addAttribute("title", shopCommonArticle.getArticleTitle());
        model.addAttribute("content", shopCommonArticle.getArticleContent());
        model.addAttribute("viewPoint",shopCommonArticle.getPageViews() == null ? 1 : (shopCommonArticle.getPageViews() + 1));
        //叠加访问量
        shopCommonArticle.setUpdateTime(new Date());
        shopCommonArticle.setPageViews(shopCommonArticle.getPageViews() == null ? 1 : (shopCommonArticle.getPageViews() + 1));
        shopCommonArticleService.update(shopCommonArticle);
        return "wap/article/detail";
    }

    @RequestMapping(value = "/wap/activity/detail/{id}", method = RequestMethod.GET)
    public String activity(ModelMap model, HttpServletRequest request, @PathVariable("id") Long activityId) {
        ShopActivity shopActivity = shopActivityService.find(activityId);
        model.addAttribute("image", shopActivity.getActivityPicture());
        List<ShopApp> shopAppList= shopAppService.findAll();
        for (ShopApp item:shopAppList) {
            if (item.getId()==1L){
                model.addAttribute("android", item.getUrl());
            }
            if (item.getId()==2L){
                model.addAttribute("ios", item.getUrl());
            }
        }

        return "wap/activity/activity_detail";
    }

    /**
     * 商品详情,合并秒杀,促销,普通商品
     *
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/wap/goods/detail/{id}", method = RequestMethod.GET)
    public String detail(ModelMap model, HttpServletRequest request, @PathVariable("id") Long goodsId) {
        if (goodsId == null) {
            return jsonFail();
        }
        //商品基本详情对象
        ShopGoods shopGoods = shopGoodsService.find(goodsId);
        if (shopGoods == null) {
            return jsonFail("商品不存在");
        }
        //评价标签
        ActivityGoodsDetailResult goodsDetailResult = ActivityGoodsDetailResult.build(shopGoods, prefix, wapServer);
        //品牌信息
        loadBrand(goodsDetailResult, shopGoods);
        //规格信息
        loadSpecInfo(goodsDetailResult, shopGoods);
        //加载品论
        loadEvaluate(goodsDetailResult);
        //加载活动
        Long activityId = loadSpeckData(goodsDetailResult,shopGoods);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("goodsDetailInfo", goodsDetailResult);
        if (activityId != null && activityId != -1) {
            ShopActivity shopActivity = shopActivityService.find(activityId);
            resultMap.put("activityInfo", ActivityDetailResult.build(shopActivity));
        }
        model.addAttribute("resultMap", resultMap);
        List<ShopApp> shopAppList= shopAppService.findAll();
        for (ShopApp item:shopAppList) {
            if (item.getId()==1L){
                model.addAttribute("android", item.getUrl());
            }
            if (item.getId()==2L){
                model.addAttribute("ios", item.getUrl());
            }
        }
        return "wap/goods/goods_detail";
    }

    //加载品牌信息
    private void loadBrand(ActivityGoodsDetailResult goodsDetailResult, ShopGoods shopGoods) {
        if (shopGoods != null && shopGoods.getBrandId() != null) {
            ShopGoodsBrand shopGoodsBrand = shopGoodsBrandService.find(shopGoods.getBrandId());
            goodsDetailResult.setBrandName(Optional.ofNullable(shopGoodsBrand.getBrandName()).orElse(""));
            goodsDetailResult.setBrandPic(Optional.ofNullable(shopGoodsBrand.getBrandPic()).orElse(""));
            goodsDetailResult.setStars(Optional.ofNullable(shopGoodsBrand.getStars()).orElse(0));
            goodsDetailResult.setBrandId(Optional.ofNullable(shopGoodsBrand.getId()).orElse(-1L));
        }
        //加载精品信息
        List<ShopGoodsRecommend> shopGoodsRecommends = shopGoodsRecommendService.findList(Paramap.create().put("goodsId", shopGoods.getId()).put("status", 1));
        if (shopGoodsRecommends.size() > 0) {
            goodsDetailResult.setIsRecomend(true);
            goodsDetailResult.setRecommendImages(shopGoodsRecommends.get(0).getRecommendImage());
        }
    }

    //商品评价
    private void loadEvaluate(ActivityGoodsDetailResult goodsDetailResult) {
        Pageable pageable = new Pageable();
        pageable.setPageNumber(1);
        pageable.setPageSize(2);
        Paramap paramap = Paramap.create().put("gevalState", 0).put("isDel", 0).put("gevalGoodsid",
                goodsDetailResult.getGoodsIs()).put("parentId", 0);
        pageable.setParameter(paramap);
        Page<ShopGoodsEvaluate> withExtends = shopGoodsEvaluateService.findWithGoodsByPage(pageable, prefix);
        goodsDetailResult.setEvaluateList(ActivityEvaluateGoodsResult.build(withExtends.getContent()));
        goodsDetailResult.setEvaluateCount(withExtends.getTotal());
    }

    private Long loadSpeckData(ActivityGoodsDetailResult goodsDetailResult,ShopGoods shopGoods) {
        List<ShopGoodsSpec> shopGoodsSpecs = shopGoodsSpecService.findListByGoodsId(goodsDetailResult.getGoodsIs());
        List<ShopActivityGoods> shopActivityGoods = shopActivityGoodsService.findList(Paramap.create()
                .put("objectId", goodsDetailResult.getGoodsIs()).put("status", 2));
        if (shopActivityGoods.size() > 0) {
            //默认取一种在线活动
            Map<Long, ShopActivityGoodsSpec> shopGoodsSpecList = shopActivityGoodsSpecService.findByAtiGoodsId(
                    shopActivityGoods.get(0).getId());
            goodsDetailResult.setProducts(ActivityGoodsSkuInfo.forList(shopGoodsSpecs, shopGoodsSpecList,shopGoods));
            return shopActivityGoods.get(0).getActivityId();
        } else {
            //完全無活動
            goodsDetailResult.setProducts(ActivityGoodsSkuInfo.forList(shopGoodsSpecs,shopGoods));
        }
        return null;
    }

    //加载规格信息
    private void loadSpecInfo(ActivityGoodsDetailResult goodsDetailResult, ShopGoods shopGoods) {
        if (shopGoods.getSpecName() == null) return;
        //规格名称
        Map<String, String> specNameMap = JacksonUtil.readJsonToMap(shopGoods.getSpecName());
        //规格值
        Map<String, List<GoodsSpecVo>> goodsSpecMap = GoodsUtils.goodsSpecStrToMapList(shopGoods.getGoodsSpec());
        List<ActivityGoodsSpesDescItem> specItemList = new ArrayList<>();
        for (Map.Entry<String, String> entry : specNameMap.entrySet()) {
            ActivityGoodsSpesDescItem spesDescItem = new ActivityGoodsSpesDescItem();
            spesDescItem.setPropId(entry.getKey());
            spesDescItem.setPropName(entry.getValue());
            List<ActivityGoodsSpesDescItem.SpesDescItemValue> values = new ArrayList<>();
            List<GoodsSpecVo> specValueList = goodsSpecMap.get(spesDescItem.getPropId());
            for (GoodsSpecVo specValueItem : specValueList) {
                ActivityGoodsSpesDescItem.SpesDescItemValue specValue = new ActivityGoodsSpesDescItem.SpesDescItemValue();
                specValue.setValueId(specValueItem.getSpValueId());
                specValue.setValue(specValueItem.getSpValueName());
                values.add(specValue);
            }
            spesDescItem.setValues(values);
            specItemList.add(spesDescItem);
        }
        goodsDetailResult.setSpecItemList(specItemList);
    }

}
