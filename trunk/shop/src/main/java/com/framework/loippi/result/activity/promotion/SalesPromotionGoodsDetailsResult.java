package com.framework.loippi.result.activity.promotion;

import com.framework.loippi.vo.ObjectUnionVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Optional;

/**
 *
 * 促销活动商品详情
 *
 */
@Data
public class SalesPromotionGoodsDetailsResult {

    /**
     * 商品销售价
     */
    private BigDecimal goodsPrice;

    /**
     * 活动商品规格id
     */
    private Long goodsSpecId;

    /**
     * 促销活动价
     */
    private BigDecimal activityPrice;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 促销所属店铺id
     */
    private Long storeId;

    public static SalesPromotionGoodsDetailsResult build(ObjectUnionVO goods){
        SalesPromotionGoodsDetailsResult item = new SalesPromotionGoodsDetailsResult();
        Optional<ObjectUnionVO> optItem = Optional.ofNullable(goods);
        item.setActivityId(optItem.map(ObjectUnionVO::getActivityId).orElse(0l));
        item.setActivityPrice(optItem.map(ObjectUnionVO::getPrice).orElse(BigDecimal.ZERO));
        item.setStoreId(optItem.map(ObjectUnionVO::getStoreId).orElse(0l));
        item.setGoodsSpecId(optItem.map(ObjectUnionVO::getGoodsSpecId).orElse(0l));
        item.setGoodsPrice(optItem.map(ObjectUnionVO::getSpecGoodsPrice).orElse(BigDecimal.ZERO));
        return item;
    }

}
