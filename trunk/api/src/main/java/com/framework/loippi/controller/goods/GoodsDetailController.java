package com.framework.loippi.controller.goods;

import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.activity.ShopActivity;
import com.framework.loippi.entity.activity.ShopActivityGoods;
import com.framework.loippi.entity.activity.ShopActivityGoodsSpec;
import com.framework.loippi.entity.common.ShopHomePicture;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsBrand;
import com.framework.loippi.entity.product.ShopGoodsBrowse;
import com.framework.loippi.entity.product.ShopGoodsEvaluate;
import com.framework.loippi.entity.product.ShopGoodsEvaluateKeywords;
import com.framework.loippi.entity.product.ShopGoodsFreightRule;
import com.framework.loippi.entity.product.ShopGoodsRecommend;
import com.framework.loippi.entity.product.ShopGoodsSpec;
import com.framework.loippi.result.activity.ActivityDetailResult;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.result.common.activity.ActivityEvaluateGoodsResult;
import com.framework.loippi.result.common.activity.ActivityGoodsDetailResult;
import com.framework.loippi.result.common.activity.ActivityGoodsSkuInfo;
import com.framework.loippi.result.common.activity.ActivityGoodsSpesDescItem;
import com.framework.loippi.service.RedisService;
import com.framework.loippi.service.activity.ShopActivityGoodsService;
import com.framework.loippi.service.activity.ShopActivityGoodsSpecService;
import com.framework.loippi.service.activity.ShopActivityService;
import com.framework.loippi.service.common.ShopHomePictureService;
import com.framework.loippi.service.product.ShopGoodsBrandService;
import com.framework.loippi.service.product.ShopGoodsBrowseService;
import com.framework.loippi.service.product.ShopGoodsEvaluateKeywordsService;
import com.framework.loippi.service.product.ShopGoodsEvaluateService;
import com.framework.loippi.service.product.ShopGoodsFreightRuleService;
import com.framework.loippi.service.product.ShopGoodsRecommendService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.product.ShopGoodsSpecService;
import com.framework.loippi.service.user.ShopMemberFavoritesService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.Constants;
import com.framework.loippi.utils.Dateutil;
import com.framework.loippi.utils.GoodsUtils;
import com.framework.loippi.utils.JacksonUtil;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.vo.goods.GoodsSpecVo;

/**
 * 商品详情新接口
 * Created by longbh on 2018/11/16.
 */
@Controller("apiGoodsDetailController")
@ResponseBody
@RequestMapping("/api/goods")
public class GoodsDetailController extends BaseController {

    @Resource
    private ShopActivityGoodsService shopActivityGoodsService;
    @Resource
    private ShopActivityGoodsSpecService shopActivityGoodsSpecService;
    @Resource
    private ShopGoodsService shopGoodsService;
    @Resource
    private ShopGoodsSpecService shopGoodsSpecService;
    @Resource
    private RedisService redisService;
    @Resource
    private ShopMemberFavoritesService shopMemberFavoritesService;
    @Resource
    private ShopGoodsEvaluateService shopGoodsEvaluateService;
    @Resource
    private ShopActivityService shopActivityService;
    @Resource
    private ShopGoodsBrandService shopGoodsBrandService;
    @Resource
    private ShopGoodsRecommendService shopGoodsRecommendService;
    @Resource
    private ShopGoodsBrowseService shopGoodsBrowseService;
    @Resource
    private ShopGoodsEvaluateKeywordsService shopGoodsEvaluateKeywordsService;
    @Resource
    private ShopGoodsFreightRuleService shopGoodsFreightRuleService;
    @Resource
    private ShopHomePictureService shopHomePictureService;

    /**
     * 商品详情,合并秒杀,促销,普通商品
     *
     * @param goodsId
     * @param activityId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "detail", method = RequestMethod.POST)
    public String detail(HttpServletRequest request, Long goodsId, Long activityId) {
        String sessionId = request.getHeader(Constants.USER_SESSION_ID);
        if (goodsId == null) {
            return jsonFail();
        }
        //商品基本详情对象
        ShopGoods shopGoods = shopGoodsService.find(goodsId);
        if (shopGoods == null) {
            return jsonFail("商品不存在");
        }
        List<ShopGoodsSpec> shopGoodsSpecs = shopGoodsSpecService.findListByGoodsId(shopGoods.getId());
        //评价标签
        ActivityGoodsDetailResult goodsDetailResult = ActivityGoodsDetailResult.build1(shopGoods,shopGoodsSpecs, prefix, wapServer);
        ShopGoodsFreightRule shopGoodsFreightRule = shopGoodsFreightRuleService.find("memberGradeId",0);
        //加载包邮金额
        goodsDetailResult.setShippingCouponAmount(shopGoodsFreightRule.getMinimumOrderAmount());
        //品牌信息
        loadBrand(goodsDetailResult, shopGoods);
        //规格信息
        loadSpecInfo(goodsDetailResult, shopGoods);
        //商品是否收藏
        loadFavorate(sessionId, goodsDetailResult,activityId);
        goodsDetailResult.setGoodsIs(goodsId);
        //加载品论
        loadEvaluate(goodsDetailResult);
        //加载品牌标语
        loadBrandDescription(goodsDetailResult,shopGoods);
        //加载关键字集合
        loadKeyWords(goodsDetailResult,shopGoods);
        //加载活动
        activityId = loadSpeckData(goodsDetailResult, activityId,shopGoods);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("goodsDetailInfo", goodsDetailResult);
        if (activityId != null && activityId!=-1) {
            ShopActivity shopActivity = shopActivityService.find(activityId);
            resultMap.put("activityInfo", ActivityDetailResult.build(shopActivity));
        }

        List<ShopHomePicture> homePictures = shopHomePictureService.findListByTypeAndStutus( 4,1);
        if (homePictures.size()>0){
            ShopHomePicture homePicture = homePictures.get(0);

            Map<String,Object> map = new HashMap<String,Object>();
            if (homePicture.getActivityUrl()!=null){
                map.put("page",homePicture.getActivityUrl());
            }else {
                map.put("page","");
            }

            if (homePicture.getPictureJson()!=null){
                Map<String, String> jsonMap = JacksonUtil.readJsonToMap(homePicture.getPictureJson());
                Set<String> strings = jsonMap.keySet();
                Iterator<String> iterator = strings.iterator();
                while (iterator.hasNext()){
                    String key = iterator.next();
                    String value = jsonMap.get(key);
                    map.put(key,value);
                }
            }

            JSONObject activityUrlJson = JSONObject.fromObject(map);
            homePicture.setActivityUrl(activityUrlJson.toString());
            resultMap.put("jumpInfo",homePicture);
        }

        return ApiUtils.success(resultMap);
    }

    private void loadKeyWords(ActivityGoodsDetailResult goodsDetailResult, ShopGoods shopGoods) {
        List<ShopGoodsEvaluateKeywords> shopGoodsEvaluateKeywordsList=shopGoodsEvaluateKeywordsService.findAll();
        List<String> stringList=new ArrayList<>();
        List<ShopGoodsEvaluateKeywords> shopGoodsEvaluateKeywordsList1=new ArrayList<>();
        List<ShopGoodsEvaluateKeywords> shopGoodsEvaluateKeywordsResult=new ArrayList<>();
        for (ShopGoodsEvaluateKeywords item:shopGoodsEvaluateKeywordsList) {
            stringList.add(item.getKeywords());
        }
        if (stringList.size()>0){
            shopGoodsEvaluateKeywordsList1=shopGoodsEvaluateService.countGevalContent(stringList,shopGoods.getId());
            for (ShopGoodsEvaluateKeywords item:shopGoodsEvaluateKeywordsList1) {
                if (item.getCountNum()!=0){
                    shopGoodsEvaluateKeywordsResult.add(item);
                }
            }
            goodsDetailResult.setShopGoodsEvaluateKeywordsList(shopGoodsEvaluateKeywordsResult);
        }
    }

    //加载收藏信息
    private void loadFavorate(String sessionId, ActivityGoodsDetailResult goodsDetailResult,Long activityId) {
        if (StringUtils.isNotBlank(sessionId)) {
            AuthsLoginResult member = redisService.get(sessionId, AuthsLoginResult.class);
            if (member != null) {
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("favId", goodsDetailResult.getGoodsIs());
                paramMap.put("memberId", Long.parseLong(member.getMmCode()));
                Long count = shopMemberFavoritesService.count(paramMap);
                goodsDetailResult.setFavGoods(count == 0 ? 0 : 1);
                //判断浏览记录今天是否有浏览该商品
                List<ShopGoodsBrowse> shopGoodsBrowseList= shopGoodsBrowseService.findList(Paramap.create().put("browseMemberId",Long.parseLong(member.getMmCode()))
                        .put("browseGoodsId",goodsDetailResult.getGoodsIs()).put("day",Dateutil.getFormatDate(new Date(),"yyyy-MM-dd")));
                //插入浏览记录
                if (shopGoodsBrowseList==null || shopGoodsBrowseList.size()<=0){
                    ShopGoodsBrowse shopGoodsBrowse = new ShopGoodsBrowse();
                    shopGoodsBrowse.setId(twiterIdService.getTwiterId());
                    shopGoodsBrowse.setBrowseMemberId(Long.parseLong(member.getMmCode()));
                    shopGoodsBrowse.setBrowseGoodsId(goodsDetailResult.getGoodsIs());
                    shopGoodsBrowse.setBrowseBrandId(goodsDetailResult.getBrandId());
                    shopGoodsBrowse.setBrowseGcId(goodsDetailResult.getClassId());
                    shopGoodsBrowse.setBrowseState(0);
                    shopGoodsBrowse.setBrowseNum(1);
                    if (activityId != null) {
                        shopGoodsBrowse.setBrowseActivityId(activityId);
                    }else{
                        shopGoodsBrowse.setBrowseActivityId(-1L);
                    }
                    shopGoodsBrowse.setCreateTime(new Date());
                    shopGoodsBrowseService.save(shopGoodsBrowse);
                }
            }
        }
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
            goodsDetailResult.setRecommendImages(Optional.ofNullable(shopGoodsRecommends.get(0).getRecommendImage()).orElse(""));
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

    //商品评价
    private void loadBrandDescription(ActivityGoodsDetailResult goodsDetailResult,ShopGoods shopGoods) {
        Long brandId = shopGoods.getBrandId();
        if(brandId!=null){
            ShopGoodsBrand shopGoodsBrand = shopGoodsBrandService.find(brandId);
            if(shopGoodsBrand!=null&&shopGoodsBrand.getDescription()!=null){
                goodsDetailResult.setDescription(shopGoodsBrand.getDescription());
            }
        }
    }

    private Long loadSpeckData(ActivityGoodsDetailResult goodsDetailResult, Long activity,ShopGoods shopGoods) {
        List<ShopGoodsSpec> shopGoodsSpecs = shopGoodsSpecService.findListByGoodsId(goodsDetailResult.getGoodsIs());
        if (activity != null) {
            //指定活动商品
            List<ShopActivityGoods> shopActivityGoods = shopActivityGoodsService.findList(Paramap.create()
                    .put("objectId", goodsDetailResult.getGoodsIs()).put("activityId", activity).put("status", 2));
            if (shopActivityGoods.size() > 0) {
                //活动商品规格
                Map<Long, ShopActivityGoodsSpec> shopGoodsSpecList = shopActivityGoodsSpecService.findByAtiGoodsId(
                        shopActivityGoods.get(0).getId());
                goodsDetailResult.setProducts(ActivityGoodsSkuInfo.forList(shopGoodsSpecs, shopGoodsSpecList,shopGoods));
                //设置商品显示价格
                goodsDetailResult.setGoodsRetailPrice(shopActivityGoods.get(0).getPrice());
//                goodsDetailResult.setStockNumber(shopActivityGoods.get(0).getStockNumber());

            } else {
                goodsDetailResult.setProducts(ActivityGoodsSkuInfo.forList(shopGoodsSpecs,shopGoods));
            }
            return activity;
        }
        else {
            goodsDetailResult.setProducts(ActivityGoodsSkuInfo.forList(shopGoodsSpecs,shopGoods));
//            List<ShopActivityGoods> shopActivityGoods = shopActivityGoodsService.findList(Paramap.create()
//                    .put("objectId", goodsDetailResult.getGoodsIs()).put("status", 2));
//            if (shopActivityGoods.size() > 0) {
//                //默认取一种在线活动
//                Map<Long, ShopActivityGoodsSpec> shopGoodsSpecList = shopActivityGoodsSpecService.findByAtiGoodsId(
//                        shopActivityGoods.get(0).getId());
//                goodsDetailResult.setProducts(ActivityGoodsSkuInfo.forList(shopGoodsSpecs, shopGoodsSpecList,shopGoods));
//                return shopActivityGoods.get(0).getActivityId();
//            }
//            else {
//                //完全無活動
//
//            }
        }
        return null;
    }

    //加载规格信息
    private void loadSpecInfo(ActivityGoodsDetailResult goodsDetailResult, ShopGoods shopGoods) {
        if (shopGoods.getSpecName() == null) return;
        if(shopGoods.getGoodsType()!=3){
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
        }else{
            List<ShopGoodsSpec> shopGoodsSpecs = shopGoodsSpecService.findListByGoodsId(goodsDetailResult.getGoodsIs());
            //规格值
            Map<String, List<GoodsSpecVo>> goodsSpecMap = GoodsUtils.goodsSpecStrToMapList(shopGoods.getGoodsSpec());
            List<ActivityGoodsSpesDescItem> specItemList = new ArrayList<>();
                ActivityGoodsSpesDescItem spesDescItem = new ActivityGoodsSpesDescItem();
                spesDescItem.setPropId("1");
                spesDescItem.setPropName("");
                List<ActivityGoodsSpesDescItem.SpesDescItemValue> values = new ArrayList<>();
                List<GoodsSpecVo> specValueList = goodsSpecMap.get(spesDescItem.getPropId());
                int i=0;
                for (ShopGoodsSpec item : shopGoodsSpecs) {
                    if (item.getSpecIsopen()!=2 && item.getSpecIsopen()!=3){
                        ActivityGoodsSpesDescItem.SpesDescItemValue specValue = new ActivityGoodsSpesDescItem.SpesDescItemValue();
                        specValue.setValueId(item.getId()+"");
                        specValue.setValue(Optional.ofNullable(item.getSpecGoodsSerial()).orElse("组合"+i));
                        values.add(specValue);
                        i++;
                    }

                }
                spesDescItem.setValues(values);
                specItemList.add(spesDescItem);

            goodsDetailResult.setSpecItemList(specItemList);
        }

    }

}
