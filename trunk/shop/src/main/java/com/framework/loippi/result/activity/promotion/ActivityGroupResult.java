package com.framework.loippi.result.activity.promotion;

import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections.CollectionUtils;

import com.framework.loippi.entity.activity.ShopActivity;

/**
 * 促销活动组
 */
@Data
public class ActivityGroupResult {

    //活动id
    private Long activityId;

    //促销活动图片
    private String activityImage;

    //排序
    private Integer sort;

    //活动标题
    private String activityName;
    //活动分享url
    private String activityShareUrl;
    //活动类型 //促销活动  1    //团购 50  //限时抢 60  //优惠券 90
    private Integer activityType;
    //优惠券id
    private Long couponId;


    public static ActivityGroupResult build(ShopActivity shopActivity, java.lang.String shareUrl) {
        Optional<ShopActivity> optItem = Optional.ofNullable(shopActivity);
        ActivityGroupResult groupResult = new ActivityGroupResult();
        groupResult.setActivityId(optItem.map(ShopActivity::getId).orElse(-1l));
        groupResult.setActivityImage(optItem.map(ShopActivity::getActivityPicture).orElse(""));
        groupResult.setSort(optItem.map(ShopActivity::getSort).orElse(0));
        groupResult.setActivityName(optItem.map(ShopActivity::getName).orElse(""));
        groupResult.setActivityShareUrl(shareUrl);
        return groupResult;
    }

    public static List<ActivityGroupResult> buildList(List<ShopActivity> items,String wapServer) {
        if (CollectionUtils.isEmpty(items)) {
            return Collections.emptyList();
        }
        List<ActivityGroupResult> infos = new ArrayList<>();
        for (ShopActivity item : items) {
            StringBuffer shareUrl = new StringBuffer();
            shareUrl.append(wapServer);
            shareUrl.append("/wap/activity/detail/");
            shareUrl.append(item.getId());
            shareUrl.append(".html");
            infos.add(ActivityGroupResult.build(item,shareUrl.toString()));
        }
        return infos;

    }

}
