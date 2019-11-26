package com.framework.loippi.result.user;

import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.entity.user.ShopMemberFavorites;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.vo.fav.MemberGoodsFavVo;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by Administrator on 2017/6/22.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCollectResult {
    private String title;
    private BigDecimal totalFee;
    private BigDecimal rental;
    private BigDecimal vipPrice;
    private String picPath;
    private Long goodsId;
    private Long id;
    private Long specId;
    private Long activityId;
    private Integer activityType;
    //状态 1正常 2失效 3售罄
    private Integer status;
    //商品类型 1普通 2换购 3组合
    private Integer goodsType;
    /**
     * 评论次数
     */
    private String commentnum="0";
    /**
     *好评率
     */
    private String appreciationRate="";

    public static List<UserCollectResult> build(List<ShopMemberFavorites> favorites, HttpServletRequest request) {
        List<UserCollectResult> results = Lists.newArrayList();
        for (ShopMemberFavorites favorite : favorites) {
            UserCollectResult result = new UserCollectResult();
            result.setId(favorite.getId());
//            Goods goods = favorite.getGoods();
//            result.setGoodsId(goods.getGoodsId());
//            result.setPicPath(HttpUtils.upload(goods.getGoodsImage()));
//            result.setTitle(goods.getGoodsName());
//            result.setRental(goods.getGoodsMarketPrice());
//            result.setTotalFee(goods.getGoodsStorePrice());
//            results.add(result);
        }
        return results;
    }

    private static UserCollectResult buildGoodsResult(MemberGoodsFavVo favorite, String prefix){

        UserCollectResult result = new UserCollectResult();

        result.setId(favorite.getId());
        result.setActivityId(Optional.ofNullable(  favorite.getActivityId()).orElse(-1L));
        result.setSpecId(Optional.ofNullable(  favorite.getSpecId()).orElse(-1L));
        result.setActivityType(Optional.ofNullable(  favorite.getActivitType()).orElse(0));
        result.setPicPath(prefix + Optional.ofNullable(  favorite.getGoodsImg()).orElse(""));
        result.setTitle(Optional.ofNullable( favorite.getGoodsName()).orElse(""));
        result.setRental(Optional.ofNullable( favorite.getMarketPrice()).orElse(BigDecimal.ZERO));
        result.setTotalFee(Optional.ofNullable( favorite.getPrice()).orElse(BigDecimal.ZERO));
        result.setGoodsId(Optional.ofNullable(  favorite.getGoodsId()).orElse(-1L));
        return result;

    }
    public static List<UserCollectResult> buildGoodsList(List<MemberGoodsFavVo> favoriteList, String prefix) {
        if(CollectionUtils.isEmpty(favoriteList)){
            return  Lists.newArrayList();
        }

        List<UserCollectResult> results = Lists.newArrayList();
        for(MemberGoodsFavVo favorites : favoriteList){

            UserCollectResult result = buildGoodsResult(favorites,prefix);
            if(result == null){
                continue;
            }
            results.add(result);
        }

        return results;
    }

    public static UserCollectResult build2(ShopGoods shopGoods,UserCollectResult result,Map<String, String> shopGoodsEvaluateMap,ShopMemberFavorites favorite,String prefix) {
        result.setId(favorite.getId());
        Long favId = favorite.getFavId();
        //if (!"".equals(shopGoodsEvaluateMap.get(favId)) && shopGoodsEvaluateMap.get(favId) != null) {
        //    result.setAppreciationRate(shopGoodsEvaluateMap.get(favId));
        //}
        result.setAppreciationRate((Optional.ofNullable(shopGoods.getEvaluaterate()).orElse(0d)*100)+"");
        result.setCommentnum(Optional.ofNullable(shopGoods.getGoodsEvaluaterate()).orElse("0"));
        result.setGoodsId(Optional.ofNullable(shopGoods.getId()).orElse(-1L));
        result.setPicPath(prefix + shopGoods.getGoodsImage());
        result.setTitle(Optional.ofNullable(shopGoods.getGoodsName()).orElse(""));
        //TODO
        result.setRental(Optional.ofNullable(shopGoods.getGoodsRetailPrice()).orElse(BigDecimal.ZERO));
        result.setTotalFee(Optional.ofNullable(shopGoods.getGoodsRetailPrice()).orElse(BigDecimal.ZERO));
        result.setVipPrice(Optional.ofNullable(shopGoods.getGoodsMemberPrice()).orElse(BigDecimal.ZERO));
        result.setActivityId(Optional.ofNullable(favorite.getActivityId()).orElse(-1L));
        result.setActivityType(Optional.ofNullable(favorite.getActivitType()).orElse(-1));
        result.setSpecId(Optional.ofNullable(favorite.getSpecId()).orElse(-1L));
        result.setGoodsType(Optional.ofNullable(shopGoods.getGoodsType()).orElse(1));
        //商品已下架或处于删除状态
        if (shopGoods.getGoodsShow() == 0 || shopGoods.getIsDel() == 1) {
            result.setStatus(2);
        } else if (shopGoods.getStock() <= 0) { //库存不够
            result.setStatus(3);
        } else {
            result.setStatus(1);
        }

        return result;
    }
}
