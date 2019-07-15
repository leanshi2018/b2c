package com.framework.loippi.result.activity.promotion;

import com.framework.loippi.vo.activity.ActivityGoodsItemVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * 促销活动--单个商品展示
 */
@Data
public class ActivityItemResult {

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 促销价
     */
    private BigDecimal activityPrice;

    /**
     * 商品销售价
     */
    private BigDecimal goodsPrice;

    /**
     * 商品默认封面图片
     */
    private String goodsImage;

    /**
     * 活动id
     */
    private Long activityId;

    /**
     * 活动商品规格id
     */
    private Long goodsSpecId;

    public static ActivityItemResult  build(ActivityGoodsItemVO item){
        Optional<ActivityGoodsItemVO> optItem = Optional.ofNullable(item);
        ActivityItemResult itemResult = new ActivityItemResult();
        itemResult.setActivityId(optItem.map(ActivityGoodsItemVO::getActivityId).orElse(0l));
        itemResult.setActivityPrice(optItem.map(ActivityGoodsItemVO::getActivityPrice).orElse(BigDecimal.ZERO));
        itemResult.setGoodsImage(optItem.map(ActivityGoodsItemVO::getGoodsImage).orElse(""));
        itemResult.setGoodsName(optItem.map(ActivityGoodsItemVO::getGoodsName).orElse(""));
        itemResult.setGoodsSpecId(optItem.map(ActivityGoodsItemVO::getGoodsSpecId).orElse(0l));
        itemResult.setGoodsPrice(optItem.map(ActivityGoodsItemVO::getGoodsPrice).orElse(BigDecimal.ZERO));
        return itemResult;
    }


}
