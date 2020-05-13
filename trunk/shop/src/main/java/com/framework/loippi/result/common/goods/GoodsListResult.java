package com.framework.loippi.result.common.goods;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.framework.loippi.entity.activity.ShopActivityGoods;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.product.ShopGoodsRecommend;
import com.framework.loippi.entity.user.ShopMemberFavorites;

/**
 * Result - 商品列表
 *
 * @author Loippi team
 * @version 2.0
 * @description 商品列表
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsListResult {

    private static final long serialVersionUID = 5081846432919091193L;

    //商品id
    @JsonSerialize(using = ToStringSerializer.class)
    private Long itemId;

    //标题
    private String title;

    private Integer goodsType;

    //零售价
    private java.math.BigDecimal goodsRetailPrice;

    //会员价格
    private java.math.BigDecimal goodsMemberPrice;

    //ppv
    private BigDecimal ppv;

    //规格名称
    private String specName;

    //销量
    private Integer salenum;

    //活动
    @JsonSerialize(using = ToStringSerializer.class)
    private Long activityId;
    private String activityType;

    //图片
    private String image;
    //默认规格ID
    private Long goodsSpecId;
    //好评率
    private Double evaluaterate;
    //评价数
    private String commentnum;
    //收藏数
    private Integer favNumber = 0;
    //是否收藏
    private Boolean favState = false;
    //商品分享url
    private String goodsShareUrl;
    //精品推荐
    private Long recommendId;
    //参与组合商品数量
    private Integer joinNum;

    public static List<GoodsListResult> build(List<ShopGoods> items, String prefix, Map<Long, ShopMemberFavorites> favoritesMap, String wapServer) {
        if (CollectionUtils.isEmpty(items)) {
            return Collections.emptyList();
        }
        List<GoodsListResult> results = new ArrayList<GoodsListResult>();
        for (ShopGoods item : items) {
            GoodsListResult result = new GoodsListResult();
            result.setItemId(Optional.ofNullable(item.getId()).orElse(-1L));
            result.setTitle(Optional.ofNullable(item.getGoodsName()).orElse(""));
            result.setImage(Optional.ofNullable(prefix + item.getGoodsImage()).orElse(""));
            result.setSalenum(Optional.ofNullable(item.getSalenum()).orElse(0));
            result.setSpecName(Optional.ofNullable(item.getSpecName()).orElse(""));
            result.setGoodsSpecId(Optional.ofNullable(item.getSpecId()).orElse(-1L));
            result.setGoodsType(Optional.ofNullable(item.getGoodsType()).orElse(1));
            result.setGoodsRetailPrice(Optional.ofNullable(item.getGoodsRetailPrice()).orElse(new BigDecimal(0)));
            result.setGoodsMemberPrice(Optional.ofNullable(item.getGoodsMemberPrice()).orElse(new BigDecimal(0)));
            result.setPpv(Optional.ofNullable(item.getPpv()).orElse(BigDecimal.ZERO));
            result.setEvaluaterate(Optional.ofNullable(item.getEvaluaterate()).orElse(0d)*100);
            result.setCommentnum(Optional.ofNullable(item.getGoodsEvaluaterate()).orElse("0"));
            result.setFavNumber(Optional.ofNullable(item.getGoodsCollect()).orElse(0));
            StringBuffer shareUrl = new StringBuffer();
            shareUrl.append(wapServer);
            shareUrl.append("/wap/goods/detail/");
            shareUrl.append(item.getId());
            shareUrl.append(".html");
            result.setGoodsShareUrl(shareUrl.toString());
            ShopMemberFavorites shopMemberFavorites = favoritesMap.get(item.getId());
            if (shopMemberFavorites != null) {
                result.setFavState(true);
            } else {
                result.setFavState(false);
            }
            results.add(result);
        }
        return results;
    }

    public static List<GoodsListResult> buildGoodsVoList(List<ShopGoods> items, Map<Long, ShopActivityGoods> goodsMap, String prefix) {
        if (CollectionUtils.isEmpty(items)) {
            return Collections.emptyList();
        }
        List<GoodsListResult> results = new ArrayList<GoodsListResult>();
        for (ShopGoods item : items) {
            GoodsListResult result = new GoodsListResult();
            result.setItemId(Optional.ofNullable(item.getId()).orElse(-1L));
            result.setTitle(Optional.ofNullable(item.getGoodsName()).orElse(""));
            result.setImage(Optional.ofNullable(prefix + item.getGoodsImage()).orElse(""));
            result.setSalenum(Optional.ofNullable(item.getSalenum()).orElse(0));
            result.setSpecName(Optional.ofNullable(item.getSpecName()).orElse(""));
            result.setGoodsSpecId(Optional.ofNullable(item.getSpecId()).orElse(-1L));
            result.setGoodsType(Optional.ofNullable(item.getGoodsType()).orElse(1));
            //设置价格
            result.setGoodsRetailPrice(Optional.ofNullable(item.getGoodsRetailPrice()).orElse(new BigDecimal(0)));
            result.setGoodsMemberPrice(Optional.ofNullable(item.getGoodsMemberPrice()).orElse(new BigDecimal(0)));
            result.setPpv(Optional.ofNullable(item.getPpv()).orElse(BigDecimal.ZERO));
            result.setEvaluaterate(Optional.ofNullable(item.getEvaluaterate()).orElse(0d)*100);
            result.setCommentnum(Optional.ofNullable(item.getGoodsEvaluaterate()).orElse("0"));
            //填充活动信息
            ShopActivityGoods nShopActivityGoods = goodsMap.get(item.getId());
            if (nShopActivityGoods != null) {
                result.setActivityId(Optional.ofNullable(nShopActivityGoods.getActivityId()).orElse(-1L));
                result.setActivityType(Optional.ofNullable(nShopActivityGoods.getActivityType()).orElse(1) + "");
                if (result.getActivityType().equals(90)) {
                    result.setActivityId(-1L);
                }
                result.setGoodsRetailPrice(Optional.ofNullable(nShopActivityGoods.getPrice()).orElse(BigDecimal.ZERO));
            } else {
                result.setActivityId(-1L);
                result.setActivityType(-1 + "");
            }

            results.add(result);
        }
        return results;
    }

    //首页精品推荐
    public static List<GoodsListResult> buildGoodsRecommendList(List<ShopGoodsRecommend> items, Map<Long, ShopMemberFavorites> favoritesMap,
        Map<Long, ShopActivityGoods> shopActivityGoodsMap,String prefix,Integer type,String wapServer) {
        if (CollectionUtils.isEmpty(items)) {
            return Collections.emptyList();
        }
        List<GoodsListResult> results = new ArrayList<GoodsListResult>();
        for (ShopGoodsRecommend item : items) {
            GoodsListResult result = new GoodsListResult();
            result.setItemId(Optional.ofNullable(item.getGoodsId()).orElse(-1L));
            result.setTitle(Optional.ofNullable(item.getGoodsName()).orElse(""));
            result.setImage(Optional.ofNullable(prefix + item.getGoodsImage()).orElse(""));
            //String recommendImage = item.getRecommendImage();
            //String[] split = recommendImage.split(",");
            //result.setImage(Optional.ofNullable(prefix + item.getRecommendImage()).orElse(""));
            //result.setImage(prefix+split[0]);
            result.setGoodsType(Optional.ofNullable(item.getGoodsType()).orElse(1));
            result.setFavNumber(Optional.ofNullable(item.getGoodsCollect()).orElse(1));
            result.setRecommendId(item.getId());
            StringBuffer shareUrl = new StringBuffer();
            shareUrl.append(wapServer);
            shareUrl.append("/wap/goods/detail/");
            shareUrl.append(item.getGoodsId());
            shareUrl.append(".html");
            result.setGoodsShareUrl(shareUrl.toString());
            if (shopActivityGoodsMap.get(item.getGoodsId())!=null){
                result.setActivityId(shopActivityGoodsMap.get(item.getGoodsId()).getActivityId());
            }else{
                result.setActivityId(-1L);
            }
            //填充活动信息
            ShopMemberFavorites shopMemberFavorites = favoritesMap.get(item.getGoodsId());
            if (type==1){
                result.setFavState(false);
            }else{
                if (shopMemberFavorites != null) {
                    result.setFavState(true);
                } else {
                    result.setFavState(false);
                }
            }
            results.add(result);
        }
        return results;
    }

    public static GoodsListResult buildItem(ShopGoods item, String prefix, String wapServer) {
        GoodsListResult result = new GoodsListResult();
        result.setItemId(Optional.ofNullable(item.getId()).orElse(-1L));
        result.setTitle(Optional.ofNullable(item.getGoodsName()).orElse(""));
        result.setImage(Optional.ofNullable(prefix + item.getGoodsImage()).orElse(""));
        result.setSalenum(Optional.ofNullable(item.getSalenum()).orElse(0));
        result.setSpecName(Optional.ofNullable(item.getSpecName()).orElse(""));
        result.setGoodsSpecId(Optional.ofNullable(item.getSpecId()).orElse(-1L));
        result.setGoodsType(Optional.ofNullable(item.getGoodsType()).orElse(1));
        result.setGoodsRetailPrice(Optional.ofNullable(item.getGoodsRetailPrice()).orElse(new BigDecimal(0)));
        result.setGoodsMemberPrice(Optional.ofNullable(item.getGoodsMemberPrice()).orElse(new BigDecimal(0)));
        result.setPpv(Optional.ofNullable(item.getPpv()).orElse(BigDecimal.ZERO));
        result.setEvaluaterate(Optional.ofNullable(item.getEvaluaterate()).orElse(0d)*100);
        result.setCommentnum(Optional.ofNullable(item.getGoodsEvaluaterate()).orElse("0"));
        result.setFavNumber(Optional.ofNullable(item.getGoodsCollect()).orElse(0));
        StringBuffer shareUrl = new StringBuffer();
        shareUrl.append(wapServer);
        shareUrl.append("/wap/goods/detail/");
        shareUrl.append(item.getId());
        shareUrl.append(".html");
        result.setGoodsShareUrl(shareUrl.toString());
        return result;
    }

}

