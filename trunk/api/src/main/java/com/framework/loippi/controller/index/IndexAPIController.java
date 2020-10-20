package com.framework.loippi.controller.index;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.framework.loippi.consts.ActivityStatus;
import com.framework.loippi.consts.GoodsState;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.activity.ShopActivity;
import com.framework.loippi.entity.activity.ShopActivityGoods;
import com.framework.loippi.entity.common.RdKeyword;
import com.framework.loippi.entity.common.ShopHomePicture;
import com.framework.loippi.entity.common.ShopRecommendationGoods;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsBrand;
import com.framework.loippi.entity.product.ShopGoodsRecommend;
import com.framework.loippi.entity.user.ShopMemberFavorites;
import com.framework.loippi.enus.ActivityTypeEnus;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.result.activity.promotion.ActivityGroupResult;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.result.common.goods.GoodsListResult;
import com.framework.loippi.result.common.index.HomeAndADPictureResult;
import com.framework.loippi.result.common.index.IndexDataResult;
import com.framework.loippi.service.RedisService;
import com.framework.loippi.service.activity.ShopActivityGoodsService;
import com.framework.loippi.service.activity.ShopActivityService;
import com.framework.loippi.service.common.RdKeywordService;
import com.framework.loippi.service.common.ShopHomePictureService;
import com.framework.loippi.service.common.ShopProductRecommendationService;
import com.framework.loippi.service.common.ShopRecommendationGoodsService;
import com.framework.loippi.service.product.ShopGoodsBrandService;
import com.framework.loippi.service.product.ShopGoodsClassService;
import com.framework.loippi.service.product.ShopGoodsRecommendService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.user.ShopMemberFavoritesService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.StringUtil;
import com.framework.loippi.utils.wechat.applets.util.GetOpenIDUtil;


@Controller
public class IndexAPIController extends BaseController {

    @Autowired
    private ShopGoodsService shopGoodsService;
    @Autowired
    private ShopActivityService shopActivityService;
    @Autowired
    private ShopActivityGoodsService shopActivityGoodsService;
    @Autowired
    private ShopMemberFavoritesService shopMemberFavoritesService;
    @Autowired
    private ShopGoodsRecommendService shopGoodsRecommendService;
    @Autowired
    private ShopGoodsClassService shopGoodsClassService;
    @Value("#{properties['wap.server']}")
    private String wapServer;
    @Resource
    private RedisService redisService;
    @Resource
    private ShopGoodsBrandService shopGoodsBrandService;
    @Resource
    private ShopHomePictureService shopHomePictureService;
    @Resource
    private RdKeywordService rdKeywordService;
    @Resource
    private ShopProductRecommendationService shopProductRecommendationService;
    @Resource
    private ShopRecommendationGoodsService shopRecommendationGoodsService;

    /**
     * 首页数据（初始化数据）
     */
    @ResponseBody
    @RequestMapping("/api/index/getData.json")
    public String getData(@RequestParam(defaultValue = "1") Integer pageNumber, HttpServletRequest request) {
        String sessionId = request.getHeader(com.framework.loippi.utils.Constants.USER_SESSION_ID);
        AuthsLoginResult member = null;
        if (StringUtils.isNotBlank(sessionId)) {
            member = redisService.get(sessionId, AuthsLoginResult.class);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        //促销活动
        map.put("activityType", ActivityTypeEnus.EnumType.zhuanchang.getValue());
        map.put("activityTime", new Date());
        map.put("activityStatus", ActivityStatus.ACTIVITY_STATUS_NOW);
        map.put("auditStatus", 1);
        Pageable pageable = new Pageable(0, 20);
        pageable.setParameter(map);
        pageable.setOrderProperty("sort");
        pageable.setOrderDirection(Order.Direction.DESC);
        Page<ShopActivity> activityList = shopActivityService.findByPage(pageable);
        //品牌精品
        Pageable goodsPage = new Pageable(pageNumber, 20);
        pageable.setOrderProperty("create_time");
        pageable.setOrderDirection(Order.Direction.DESC);
        List<ShopGoodsBrand> shopGoodsBrandList = shopGoodsBrandService
            .findList(Paramap.create().put("brandRecommend", 0));
        List<Long> noBrandIds = new ArrayList<>();
        if (shopGoodsBrandList != null && shopGoodsBrandList.size() > 0) {
            for (ShopGoodsBrand item : shopGoodsBrandList) {
                noBrandIds.add(item.getId());
            }

        }
        goodsPage.setParameter(
            Paramap.create().put("status", 1).put("noBrandIds", noBrandIds).put("activityTime", new Date()));
        goodsPage.setOrderProperty("sort");
        goodsPage.setOrderDirection(Order.Direction.DESC);
        Page<ShopGoodsRecommend> recommendPage = shopGoodsRecommendService.findByPage(goodsPage);
        List<Long> goodIds = new ArrayList<>();
        for (ShopGoodsRecommend item : recommendPage.getContent()) {
            goodIds.add(item.getGoodsId());
        }
        //收藏消息
        Map<Long, ShopMemberFavorites> shopMemberFavoritesMap = shopMemberFavoritesService.findFavoriteMap(goodIds);
        //活动消息
        Map<Long, ShopActivityGoods> shopActivityGoodsMap=findActivityId(goodIds);
        //数据封装
        IndexDataResult result = new IndexDataResult();
        result.setActivityGroupInfo(ActivityGroupResult.buildList(activityList.getContent(), wapServer));//促销活动商品列表
        if (member == null) {
            result.setGoodsListResult(GoodsListResult
                .buildGoodsRecommendList(recommendPage.getContent(), shopMemberFavoritesMap,shopActivityGoodsMap, prefix, 1, wapServer));
        } else {
            List<ShopMemberFavorites> favoriteGoods = new ArrayList<>();
            favoriteGoods = shopMemberFavoritesService
                .findList(Paramap.create().put("memberId", member.getMmCode()).put("favType", 1));
            shopMemberFavoritesMap.clear();
            for (ShopMemberFavorites item : favoriteGoods) {
                shopMemberFavoritesMap.put(item.getFavId(), item);
            }
            result.setGoodsListResult(GoodsListResult
                .buildGoodsRecommendList(recommendPage.getContent(), shopMemberFavoritesMap,shopActivityGoodsMap, prefix, 2, wapServer));
        }
        return ApiUtils.success(result);
    }

    /**
     * 商品列表
     * @param categoryId 商品分类id
     * @param goodsType 商品类型 1-普通2-积分3-组合
     * @param pageSize
     * @param sortField 排序字段
     * @param pageNumber
     * @param orderBy 升序或者降序
     * @param keyword 关键字
     * @param categoryIds 商品分类集合
     * @param brandIds 品牌集合
     * @param minPrice 最低价格
     * @param maxPrice 最大价格
     * @param activityId 活动id
     * @param rId 推荐页id
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/index/search.json", method = RequestMethod.POST)
    @ResponseBody
    public String search(
        Long categoryId,
        @RequestParam(required = false, value = "goodsType") Integer goodsType,
        @RequestParam(required = false, value = "pageSize", defaultValue = "10") Integer pageSize,
        String sortField,
        @RequestParam(required = false, value = "pageNumber", defaultValue = "1") Integer pageNumber,
        @RequestParam(required = false, value = "orderBy", defaultValue = "1") Integer orderBy,
        String keyword, Long[] categoryIds, Long[] brandIds, BigDecimal minPrice,
        BigDecimal maxPrice, Long activityId,Long rId, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        Pageable pager = new Pageable();
        Map<String, Object> shopGoodsVo = new HashMap<>();
        pager.setPageNumber(pageNumber);
        pager.setPageSize(pageSize);
        pager.setOrderDirection(orderBy.equals(1) ? Order.Direction.ASC : Order.Direction.DESC);
        if (brandIds != null && brandIds.length > 0) {
            List<Long> brandList = Arrays.asList(brandIds);
            shopGoodsVo.put("brandIds", brandList);
        }
        if (categoryId != null) {
            shopGoodsVo.put("gcId", categoryId);
        }
        if (categoryIds != null && categoryIds.length > 0) {
            List<Long> classList = Arrays.asList(categoryIds);
            //查询所有子节点
            List<Long> longList = shopGoodsClassService.findIdsByParentIds(classList);
            longList.addAll(classList);
            if (longList != null && longList.size() > 0) {
                shopGoodsVo.put("classIds", longList);
            }

        }
        if (goodsType != null) {
            if (goodsType == 1) {
                shopGoodsVo.put("goodsType", 1);
            } else {
                shopGoodsVo.put("goodsType", 2);
            }
        } else {
            shopGoodsVo.put("noGoodsType", 2);
        }

        shopGoodsVo.put("isDels", GoodsState.GOODS_NOT_DELETE);
        shopGoodsVo.put("goodsShow", GoodsState.GOODS_ON_SHOW);
        shopGoodsVo.put("state", GoodsState.GOODS_OPEN_STATE);
        if (!StringUtil.isEmpty(sortField)) {
            if ("orderPrice".equals(sortField)) {
                pager.setOrderProperty("goods_retail_price");
            }
            if ("evaluaterate".equals(sortField)) {
                shopGoodsVo.put("goodsEvaluaterate", "goodsEvaluaterate");
            }
            if ("salenum".equals(sortField)) {
                shopGoodsVo.put("goodsSalenum", "goodsSalenum");
            }
        } else {
            shopGoodsVo.put("orderAll", GoodsState.DEFAULT_ORDER);
        }
        if (minPrice != null) {
            shopGoodsVo.put("minPrice", minPrice);
        }
        if (maxPrice != null) {
            shopGoodsVo.put("maxPrice", maxPrice);
        }
        if (!StringUtil.isEmpty(keyword)) {
            shopGoodsVo.put("goodsListKeywords", keyword);
        }
        if (activityId != null) {
            List<ShopActivityGoods> shopActivityGoodsList = shopActivityGoodsService.findList("activityId", activityId);
            List<Long> longList = new ArrayList<>();
            for (int i = 0; i < shopActivityGoodsList.size(); i++) {
                longList.add(shopActivityGoodsList.get(i).getObjectId());
            }
            if (longList.size() > 0) {
                shopGoodsVo.put("ids", longList);
            } else {
                longList.add(-1L);
                shopGoodsVo.put("ids", longList);
            }
        }
        if (rId != null) {
            List<ShopRecommendationGoods> shopRecommendationGoodsList = shopRecommendationGoodsService.findList("rId", rId);
            List<Long> longList = new ArrayList<>();
            for (int i = 0; i < shopRecommendationGoodsList.size(); i++) {
                longList.add(shopRecommendationGoodsList.get(i).getGoodsId());
            }
            if (longList.size() > 0) {
                shopGoodsVo.put("ids", longList);
            } else {
                longList.add(-1L);
                shopGoodsVo.put("ids", longList);
            }
        }
        //List<ShopGoodsBrand> shopGoodsBrandList = shopGoodsBrandService
        //    .findList(Paramap.create().put("brandRecommend", 0));
        //List<Long> noBrandIds = new ArrayList<>();
        //if (shopGoodsBrandList != null && shopGoodsBrandList.size() > 0) {
        //    for (ShopGoodsBrand item : shopGoodsBrandList) {
        //        noBrandIds.add(item.getId());
        //    }
        //
        //}
        shopGoodsVo.put("noBrandIds", "1");
        shopGoodsVo.put("showCommentNum",1);
        pager.setParameter(shopGoodsVo);
        Page<ShopGoods> byPage = shopGoodsService.findByPage(pager);// 结果集
        //填充活动信息/推荐页信息
        List<GoodsListResult> build = new ArrayList<>();
        if (rId!=null){
            build = GoodsListResult.buildGoodsRecommendListNew(byPage.getContent(), prefix);
        }else {
            build = shopActivityGoodsService.findAndAddAtiInfo(byPage.getContent(), prefix);
        }
        map.put("GoodsList", build);
        return ApiUtils.success(map);
    }

    /**
     * plus vip 商品列表
     * @param categoryId 商品分类id
     * @param goodsType 商品类型 1-普通2-积分3-组合
     * @param pageSize
     * @param sortField 排序字段
     * @param pageNumber
     * @param orderBy 升序或者降序
     * @param keyword 关键字
     * @param categoryIds 商品分类集合
     * @param brandIds 品牌集合
     * @param minPrice 最低价格
     * @param maxPrice 最大价格
     * @param activityId 活动id
     * @param rId 推荐页id
     * @param request
     * @return
     */
    @RequestMapping(value = "/api/index/searchPlusVipGoods.json", method = RequestMethod.POST)
    @ResponseBody
    public String searchPlusVipGoods(
            Long categoryId,
            @RequestParam(required = false, value = "goodsType") Integer goodsType,
            @RequestParam(required = false, value = "pageSize", defaultValue = "10") Integer pageSize,
            String sortField,
            @RequestParam(required = false, value = "pageNumber", defaultValue = "1") Integer pageNumber,
            @RequestParam(required = false, value = "orderBy", defaultValue = "1") Integer orderBy,
            String keyword, Long[] categoryIds, Long[] brandIds, BigDecimal minPrice,
            BigDecimal maxPrice, Long activityId,Long rId, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        Pageable pager = new Pageable();
        Map<String, Object> shopGoodsVo = new HashMap<>();
        pager.setPageNumber(pageNumber);
        pager.setPageSize(pageSize);
        pager.setOrderDirection(orderBy.equals(1) ? Order.Direction.ASC : Order.Direction.DESC);
        if (brandIds != null && brandIds.length > 0) {
            List<Long> brandList = Arrays.asList(brandIds);
            shopGoodsVo.put("brandIds", brandList);
        }
        if (categoryId != null) {
            shopGoodsVo.put("gcId", categoryId);
        }
        if (categoryIds != null && categoryIds.length > 0) {
            List<Long> classList = Arrays.asList(categoryIds);
            //查询所有子节点
            List<Long> longList = shopGoodsClassService.findIdsByParentIds(classList);
            longList.addAll(classList);
            if (longList != null && longList.size() > 0) {
                shopGoodsVo.put("classIds", longList);
            }

        }
        if (goodsType != null) {
            if (goodsType == 1) {
                shopGoodsVo.put("goodsType", 1);
            } else {
                shopGoodsVo.put("goodsType", 2);
            }
        } else {
            shopGoodsVo.put("noGoodsType", 2);
        }

        shopGoodsVo.put("isDels", GoodsState.GOODS_NOT_DELETE);
        shopGoodsVo.put("goodsShow", GoodsState.GOODS_ON_SHOW);
        shopGoodsVo.put("state", GoodsState.GOODS_OPEN_STATE);
        if (!StringUtil.isEmpty(sortField)) {
            if ("orderPrice".equals(sortField)) {
                pager.setOrderProperty("goods_retail_price");
            }
            if ("evaluaterate".equals(sortField)) {
                shopGoodsVo.put("goodsEvaluaterate", "goodsEvaluaterate");
            }
            if ("salenum".equals(sortField)) {
                shopGoodsVo.put("goodsSalenum", "goodsSalenum");
            }
        } else {
            shopGoodsVo.put("orderAll", GoodsState.DEFAULT_ORDER);
        }
        if (minPrice != null) {
            shopGoodsVo.put("minPrice", minPrice);
        }
        if (maxPrice != null) {
            shopGoodsVo.put("maxPrice", maxPrice);
        }
        if (!StringUtil.isEmpty(keyword)) {
            shopGoodsVo.put("goodsListKeywords", keyword);
        }
        if (activityId != null) {
            List<ShopActivityGoods> shopActivityGoodsList = shopActivityGoodsService.findList("activityId", activityId);
            List<Long> longList = new ArrayList<>();
            for (int i = 0; i < shopActivityGoodsList.size(); i++) {
                longList.add(shopActivityGoodsList.get(i).getObjectId());
            }
            if (longList.size() > 0) {
                shopGoodsVo.put("ids", longList);
            } else {
                longList.add(-1L);
                shopGoodsVo.put("ids", longList);
            }
        }
        if (rId != null) {
            List<ShopRecommendationGoods> shopRecommendationGoodsList = shopRecommendationGoodsService.findList("rId", rId);
            List<Long> longList = new ArrayList<>();
            for (int i = 0; i < shopRecommendationGoodsList.size(); i++) {
                longList.add(shopRecommendationGoodsList.get(i).getGoodsId());
            }
            if (longList.size() > 0) {
                shopGoodsVo.put("ids", longList);
            } else {
                longList.add(-1L);
                shopGoodsVo.put("ids", longList);
            }
        }
        //List<ShopGoodsBrand> shopGoodsBrandList = shopGoodsBrandService
        //    .findList(Paramap.create().put("brandRecommend", 0));
        //List<Long> noBrandIds = new ArrayList<>();
        //if (shopGoodsBrandList != null && shopGoodsBrandList.size() > 0) {
        //    for (ShopGoodsBrand item : shopGoodsBrandList) {
        //        noBrandIds.add(item.getId());
        //    }
        //
        //}
        shopGoodsVo.put("noBrandIds", "1");
        shopGoodsVo.put("showCommentNum",1);
        shopGoodsVo.put("plusVipType",1);
        pager.setParameter(shopGoodsVo);
        Page<ShopGoods> byPage = shopGoodsService.findByPage(pager);// 结果集
        //填充活动信息/推荐页信息
        List<GoodsListResult> build = new ArrayList<>();
        if (rId!=null){
            build = GoodsListResult.buildGoodsRecommendListNew(byPage.getContent(), prefix);
        }else {
            build = shopActivityGoodsService.findAndAddAtiInfo(byPage.getContent(), prefix);
        }
        map.put("GoodsList", build);
        return ApiUtils.success(map);
    }

    public Map<Long, ShopActivityGoods> findActivityId( List<Long> goodIds){
        Map<Long, ShopActivity> shopActivityMap = new HashMap<>();
        Map<Long, ShopActivityGoods> shopActivityGoodsMap = new HashMap<>();
        if(goodIds.size()>0){
            List<ShopActivityGoods> shopActivityGoodses = shopActivityGoodsService.findList(Paramap.create().put("goodIdList", goodIds).put("status", 2));
            goodIds.clear();
            for (ShopActivityGoods shopActivityGoods : shopActivityGoodses) {
                goodIds.add(shopActivityGoods.getActivityId());
            }
            if (goodIds.size()>0){
                List<ShopActivity> shopActivityList=shopActivityService.findList(Paramap.create().put("ids",goodIds));

                for (ShopActivity item:shopActivityList) {
                    if (item.getStartTime().getTime()>new Date().getTime()){
                        shopActivityMap.put(item.getId(),item);
                    }else{
                        if (item.getEndTime().getTime()<new Date().getTime()){
                            shopActivityMap.put(item.getId(),item);
                        }
                    }
                }
            }
            for (ShopActivityGoods shopActivityGoods : shopActivityGoodses) {
                if (shopActivityMap.get(shopActivityGoods.getActivityId())==null)
                {
                    shopActivityGoodsMap.put(shopActivityGoods.getObjectId(), shopActivityGoods);
                }
            }
        }

             return  shopActivityGoodsMap;
    }

    /**
     * 轮播图或广告位列表
     * @return
     */
	@ResponseBody
    @RequestMapping("/api/index/getHomePicture.json")
    public String getHomePicture() {

        //轮播图
        List<ShopHomePicture> homePictures = shopHomePictureService.findListByTypeAndStutus( 0,1);

        //广告位图
        List<ShopHomePicture> adPictures = shopHomePictureService.findListByTypeAndStutus(1,1);

		List<RdKeyword> keywordList = rdKeywordService.findByAll();
		return ApiUtils.success(HomeAndADPictureResult.build(homePictures,adPictures,keywordList));
    }

    /**
     * 新轮播图或广告位列表
     * @return
     */
	@ResponseBody
    @RequestMapping("/api/index/getHomePictureNew.json")
    public String getHomePictureNew() {

        //轮播图
        List<ShopHomePicture> homePictures = shopHomePictureService.findListByTypeAndStutus( 0,1);

        //广告位图
        List<ShopHomePicture> adPictures = shopHomePictureService.findListByTypeAndStutus(2,1);

		List<RdKeyword> keywordList = rdKeywordService.findByAll();
		return ApiUtils.success(HomeAndADPictureResult.build(homePictures,adPictures,keywordList));
    }

    public static String appid = "wx6e94bb18bedf3c4c";//公众账号ID
    public static String appsecret = "c7af91f8b99593a2073f6e691f8ebfc4";//应用密钥
    @RequestMapping(value = {"/api/index/getOpenId.json"}, method = {RequestMethod.GET,
            RequestMethod.POST})
    public
    @ResponseBody
    Object getOpenId(String code) {
        if (code == null || code.length() == 0) {
            throw new NullPointerException("code不能为空!");
        }
        return GetOpenIDUtil.oauth2GetOpenid(appid, code, appsecret);
    }

    @RequestMapping(value = {"/api/index/getPhone.json"}, method = {RequestMethod.GET,
            RequestMethod.POST})
    public
    @ResponseBody
    Object getPhone(String encrypted,String sessionKey,String iv) throws Exception {
        return GetOpenIDUtil.wxDecrypt(encrypted, sessionKey, iv);
    }



}