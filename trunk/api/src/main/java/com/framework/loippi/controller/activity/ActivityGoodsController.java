package com.framework.loippi.controller.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.activity.ShopActivity;
import com.framework.loippi.entity.activity.ShopActivityGoods;
import com.framework.loippi.entity.common.ShopProductRecommendation;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.enus.ActivityTypeEnus;
import com.framework.loippi.mybatis.paginator.domain.Order;
import com.framework.loippi.result.activity.promotion.SalesPromotionGoodsResult;
import com.framework.loippi.service.activity.ShopActivityGoodsService;
import com.framework.loippi.service.activity.ShopActivityService;
import com.framework.loippi.service.common.ShopProductRecommendationService;
import com.framework.loippi.service.common.ShopRecommendationGoodsService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.StringUtil;

/**
 * 新品首发相关接口
 * Created by longbh on 2018/11/13.
 */
@Controller("apiShopFreshController")
@ResponseBody
@RequestMapping("/api/shopActivity")
public class ActivityGoodsController extends BaseController {

    @Resource
    private ShopActivityService shopActivityService;
    @Resource
    private ShopActivityGoodsService shopActivityGoodsService;
    @Resource
    private ShopGoodsService shopGoodsService;
    @Resource
    private ShopProductRecommendationService shopProductRecommendationService;
    @Resource
    private ShopRecommendationGoodsService shopRecommendationGoodsService;

    /**
     * 促销活动查看更多商品信息
     *
     * @param pageNumber 页码
     * @return
     */
    @RequestMapping(value = "/detail", method = RequestMethod.POST)
    public String salesPromotionList(
            Long activityId, String sortField,
            @RequestParam(required = false, value = "orderBy", defaultValue = "1") Integer orderBy,
            @RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber) {
        if (activityId==null){
            return  ApiUtils.error("活动id为空");
        }
        //促销活动商品列表
        Pageable pager = new Pageable();
        pager.setPageSize(20);
        pager.setPageNumber(pageNumber);
        pager.setOrderDirection(orderBy.equals(1) ? Order.Direction.ASC : Order.Direction.DESC);
        Map<String, Object> condition = new HashMap<>();
        condition.put("activityTime", new Date());
        condition.put("activityType", ActivityTypeEnus.EnumType.zhuanchang.getValue());
        condition.put("activityId", activityId);
        condition.put("status", 2);//活动开关
        //TODO
        if (!StringUtil.isEmpty(sortField)) {
            if ("orderPrice".equals(sortField)) {
                pager.setOrderProperty("goods_market_price");
            } else {
                pager.setOrderProperty(sortField);
            }
        } else {
            pager.setOrderProperty("salenum");
        }
        pager.setParameter(condition);
        Page<ShopActivityGoods> objects = shopActivityGoodsService.findByPage(pager);
        List<ShopActivityGoods> shopActivityGoodsList=objects.getContent();
        List<ShopGoods> shopGoodsList=new ArrayList<>();
        if (shopActivityGoodsList!=null && shopActivityGoodsList.size()>0){
            List<Long> ids=new ArrayList<>();
            for (ShopActivityGoods item:shopActivityGoodsList) {
                ids.add(item.getObjectId());
            }
            shopGoodsList=shopGoodsService.findList(Paramap.create().put("ids",ids));

        }
        Map<String, Object> resultMap = new HashMap<String, Object>();
        //封装数据
        resultMap.put("activityGoodslist", SalesPromotionGoodsResult.buildList(objects.getContent() ,prefix,shopGoodsList));//商品分组下的商品列表
        if (pageNumber == 1) {
            //查询正在进行中的首发活动
            ShopActivity shopActivity = shopActivityService.find(activityId);
            if (shopActivity!=null){
                resultMap.put("activityName", shopActivity.getActivityName());
                resultMap.put("activityImage", shopActivity.getActivityPicture());
                resultMap.put("activityId", activityId);
            }else{
                return ApiUtils.error("活动不存在");
            }
        }
        return ApiUtils.success(resultMap);
    }

    /**
     * 获取活动专场详情（名称和活动图片)
     */
    @RequestMapping(value = "/info", method = RequestMethod.POST)
    public String info(Long activityId) {
        ShopActivity shopActivity = shopActivityService.find(activityId);

        return ApiUtils.success(Paramap.create().put("activityName", shopActivity.getActivityName())
                .put("activityPicture", shopActivity.getActivityPicturePc()));
    }

    /**
     * 获取推荐页专场详情（名称和活动图片)
     */
    @RequestMapping(value = "/recommendationInfo", method = RequestMethod.POST)
    public String recommendationInfo(Long rId) {
        ShopProductRecommendation recommendation = shopProductRecommendationService.find(rId);

        return ApiUtils.success(Paramap.create().put("recommendationName", recommendation.getRecommendationName())
                .put("pictureUrl", recommendation.getPictureUrl()));
    }

}
