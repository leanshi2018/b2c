package com.framework.loippi.controller.goods;


import com.framework.loippi.consts.Constants;
import com.framework.loippi.consts.GoodsState;
import com.framework.loippi.controller.BaseController;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsGoods;
import com.framework.loippi.entity.product.ShopGoodsRecommend;
import com.framework.loippi.result.auths.AuthsLoginResult;
import com.framework.loippi.result.common.goods.GoodsListResult;
import com.framework.loippi.service.activity.ShopActivityGoodsService;
import com.framework.loippi.service.product.ShopGoodsGoodsService;
import com.framework.loippi.service.product.ShopGoodsRecommendService;
import com.framework.loippi.service.product.ShopGoodsService;
import com.framework.loippi.service.user.ShopMemberFavoritesService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.ApiUtils;
import com.framework.loippi.utils.Paramap;
import com.framework.loippi.utils.Xerror;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by longbh on 2017/7/26.
 */
@Controller
public class GoodsController extends BaseController {

    @Autowired
    private ShopMemberFavoritesService shopMemberFavoritesService;
    @Autowired
    private ShopGoodsGoodsService shopGoodsGoodsService;
    @Autowired
    private ShopActivityGoodsService shopActivityGoodsService;
    @Autowired
    private ShopGoodsService shopGoodsService;
    @Autowired
    private ShopGoodsRecommendService shopGoodsRecommendService;

    /**
     * 收藏和取消收藏
     */
    @ResponseBody
    @RequestMapping("/api/fav/favGoods.json")
    public String favGoods(Long goodsId, Long storeId, Integer type,Long activityId ,HttpServletRequest request) {
        AuthsLoginResult member = (AuthsLoginResult) request.getAttribute(Constants.CURRENT_USER);
        Paramap paramap = Paramap.create();
        if (type == 1) {
            paramap.put("favId", goodsId);
        } else if (type == 2) {
            goodsId = storeId;
            paramap.put("favId", storeId);
        }
        paramap.put("memberId", Long.parseLong(member.getMmCode()));
        paramap.put("favType", 1);
        Long favoriteGoodsCount = shopMemberFavoritesService.count(paramap);

        if (favoriteGoodsCount <= 0) {
            shopMemberFavoritesService.saveFavGoods(goodsId,activityId, Long.parseLong(member.getMmCode()), type);
            return ApiUtils.success("收藏成功");
        } else {
            shopMemberFavoritesService.deleteFavGoods(goodsId, Long.parseLong(member.getMmCode()), type);
            return ApiUtils.success("取消收藏成功");
        }
    }

    /**
     * 批量加入收藏
     *
     * @param goodsIds 商品id集合 ,号隔开
     */
    @ResponseBody
    @RequestMapping("/api/fav/batchFavGoods.json")
    public String batchFavGoods(String goodsIds,String activityIds, HttpServletRequest request) {
        if (StringUtils.isEmpty(goodsIds)) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        if (goodsIds.contains("-1")) {
            return ApiUtils.error("包含已失效商品");
        }
        AuthsLoginResult member = (AuthsLoginResult) request
            .getAttribute(com.framework.loippi.utils.Constants.CURRENT_USER);
        String[] idsArray = goodsIds.split(",");
        Long[] idsLong = new Long[idsArray.length];
        String[] activityIdsArray = activityIds.split(",");
        Long[] activityIdsLong = new Long[activityIdsArray.length];
        for (int i = 0; i < idsArray.length; i++) {
            idsLong[i] = Long.valueOf(idsArray[i]);
        }
        for (int i = 0; i < activityIdsArray.length; i++) {
            activityIdsLong[i] = Long.valueOf(activityIdsArray[i]);
        }
        if (idsLong.length > 0) {
            shopMemberFavoritesService.batchSaveFavGoods(idsLong,activityIdsLong, Long.parseLong(member.getMmCode()), 1);
        } else {
            return ApiUtils.error("商品id有误");
        }
        return ApiUtils.success();
    }

    /**
     * 套组商品
     */
    @ResponseBody
    @RequestMapping("/api/goods/combine.json")
    public String combine(Long goodsId, Long combileId, HttpServletRequest request,
        @RequestParam(defaultValue = "1") Integer pageNumber) {
        if (goodsId == null) {
            return ApiUtils.error(Xerror.PARAM_INVALID);
        }
        Map<String, Object> resultMap = new HashMap<>();
        List<ShopGoodsGoods> shopGoodsGoodsList = shopGoodsGoodsService
            .findList(Paramap.create().put("goodId", goodsId));
        List<Long> combineGoods = shopGoodsGoodsList.stream()
            .map(ShopGoodsGoods::getCombineGoodsId)
            .collect(Collectors.toList());
        //TODO 优先
        //if(combineGoods.size() == 0) {
        Pageable pageable = new Pageable(pageNumber, 10);
        pageable.setParameter(Paramap.create().put("ids", combineGoods)
            .put("isDel", GoodsState.GOODS_NOT_DELETE)
            .put("goodsShow", GoodsState.GOODS_ON_SHOW)
            .put("state", GoodsState.GOODS_OPEN_STATE));
        Page<ShopGoods> shopGoodsGoodsPage = shopGoodsService.findByPage(pageable);
        //填充活动信息
        List<GoodsListResult> build = shopActivityGoodsService
            .findAndAddAtiInfo(shopGoodsGoodsPage.getContent(), prefix);
        if (combileId != null) {
            ShopGoodsRecommend shopGoodsRecommend = shopGoodsRecommendService.find(combileId);
            if (shopGoodsRecommend != null) {
                resultMap.put("recommendImage", shopGoodsRecommend.getRecommendImage());
            }
            ShopGoods shopGoods = shopGoodsService.find(goodsId);
            if (shopGoods != null && shopGoods.getGoodsType() == 3) {
                build.add(0, GoodsListResult.buildItem(shopGoods, prefix, wapServer));
            }
        }
        resultMap.put("goodsList", build);
        return ApiUtils.success(resultMap);
//        }else{
//            return ApiUtils.success(new ArrayList<>());
//        }
    }

}
