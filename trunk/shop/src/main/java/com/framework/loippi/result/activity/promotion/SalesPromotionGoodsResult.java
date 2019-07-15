package com.framework.loippi.result.activity.promotion;

import com.framework.loippi.entity.activity.ShopActivityGoods;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.mybatis.ext.annotation.Column;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 促销活动商品列表对象
 */
@Data
public class SalesPromotionGoodsResult {

    private Long goodsId;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品默认封面图片
     */
    private String goodsImage;

    //零售价
    private java.math.BigDecimal goodsRetailPrice;

    //会员价格
    private java.math.BigDecimal goodsMemberPrice;

    //ppv
    private BigDecimal ppv;

    //商品已销售件数
    private Integer saleNum;

    /**
     * 活动类型
     */
    private Integer activityType;

    private Long activityId;
    /**
     * 商品评价数
     */
    private Integer commentnum;
    /**
     * 好评率
     */
    private Double evaluaterate;


    public static SalesPromotionGoodsResult build(ShopActivityGoods goods, String prefix) {
        Optional<ShopActivityGoods> optItem = Optional.ofNullable(goods);
        SalesPromotionGoodsResult item = new SalesPromotionGoodsResult();
        item.setGoodsRetailPrice(optItem.map(ShopActivityGoods::getGoodsRetailPrice).orElse(BigDecimal.ZERO));
        item.setGoodsImage(prefix + optItem.map(ShopActivityGoods::getGoodsImage).orElse(""));
        item.setGoodsName(optItem.map(ShopActivityGoods::getGoodsName).orElse(""));
        item.setGoodsId(optItem.map(ShopActivityGoods::getObjectId).orElse(0l));
        item.setGoodsMemberPrice(optItem.map(ShopActivityGoods::getGoodsMemberPrice).orElse(BigDecimal.ZERO));
        item.setPpv(optItem.map(ShopActivityGoods::getPpv).orElse(BigDecimal.ZERO));
        item.setSaleNum(optItem.map(ShopActivityGoods::getSaleNumber).orElse(0));
        item.setActivityType(optItem.map(ShopActivityGoods::getActivityType).orElse(0));
        item.setEvaluaterate(0d);
        item.setCommentnum(0);
        return item;
    }

    public static List<SalesPromotionGoodsResult> buildList(List<ShopActivityGoods> items ,String prefix,List<ShopGoods> shopGoodsList) {
        if (CollectionUtils.isEmpty(items)) {
            return Collections.emptyList();
        }
        Map<Long, ShopGoods> shopGoodsMap = new HashMap<>();
        if (shopGoodsList!=null && shopGoodsList.size()>0){
            for (ShopGoods item:shopGoodsList) {
                shopGoodsMap.put(item.getId(),item);
            }

        }
        List<SalesPromotionGoodsResult> infos = new ArrayList<>();
        for (ShopActivityGoods item : items) {
            infos.add(SalesPromotionGoodsResult.build(item,shopGoodsMap.get(item.getObjectId()), prefix));
        }
        return infos;
    }

    public static SalesPromotionGoodsResult build(ShopActivityGoods goods, ShopGoods shopGoods, String prefix) {
        Optional<ShopActivityGoods> optItem = Optional.ofNullable(goods);
        Optional<ShopGoods> goodsItem = Optional.ofNullable(shopGoods);
        SalesPromotionGoodsResult item = new SalesPromotionGoodsResult();
        item.setGoodsRetailPrice(goodsItem.map(ShopGoods::getGoodsRetailPrice).orElse(BigDecimal.ZERO));
        item.setGoodsImage(prefix + goodsItem.map(ShopGoods::getGoodsImage).orElse(""));
        item.setGoodsName(goodsItem.map(ShopGoods::getGoodsName).orElse(""));
        item.setGoodsId(goodsItem.map(ShopGoods::getId).orElse(0l));
        item.setGoodsMemberPrice(optItem.map(ShopActivityGoods::getGoodsMemberPrice).orElse(BigDecimal.ZERO));
        item.setPpv(optItem.map(ShopActivityGoods::getPpv).orElse(BigDecimal.ZERO));
        item.setSaleNum(optItem.map(ShopActivityGoods::getSaleNumber).orElse(0));
        item.setActivityType(optItem.map(ShopActivityGoods::getActivityType).orElse(0));
        item.setEvaluaterate(goodsItem.map(ShopGoods::getEvaluaterate).orElse(0d)*100);
        item.setCommentnum(goodsItem.map(ShopGoods::getCommentnum).orElse(0));
        return item;
    }

    public static List<SalesPromotionGoodsResult> buildList(List<ShopActivityGoods> items, Map<Long, ShopGoods> shopGoodsMap, String prefix) {
        if (CollectionUtils.isEmpty(items)) {
            return Collections.emptyList();
        }
        List<SalesPromotionGoodsResult> infos = new ArrayList<>();
        for (ShopActivityGoods item : items) {
            infos.add(SalesPromotionGoodsResult.build(item, shopGoodsMap.get(item.getObjectId()), prefix));
        }
        return infos;
    }


}
