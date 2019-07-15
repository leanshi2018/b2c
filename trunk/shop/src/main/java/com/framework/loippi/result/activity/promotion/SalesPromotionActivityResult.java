package com.framework.loippi.result.activity.promotion;

import com.framework.loippi.entity.activity.ShopActivity;
import lombok.Data;

import java.util.Optional;

/**
 * 促销活动对象
 */
@Data
public class SalesPromotionActivityResult {

    private Long activityId;//促销活动id

    private Long groupId;//促销商品分组id

    private String activityName;//活动名称

    private Integer activityType;//活动类型

    private Integer activityStatus;//活动类型   活动状态  10  未开始  20 活动中 30已结束

    public  static SalesPromotionActivityResult  build(ShopActivity shopActivity, Long groupId){
        SalesPromotionActivityResult item = new SalesPromotionActivityResult();
        Optional<ShopActivity> optItem = Optional.ofNullable(shopActivity);
        item.setActivityId(optItem.map(ShopActivity::getId).orElse(0l));
        item.setActivityName(optItem.map(ShopActivity::getActivityName).orElse(""));
        item.setActivityType(optItem.map(ShopActivity::getActivityType).orElse(0));
        item.setActivityStatus(optItem.map(ShopActivity::getActivityStatus).orElse(0));
        item.setGroupId(groupId==null?0:groupId);
        return item;
    }

}
