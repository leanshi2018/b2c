package com.framework.loippi.result.activity;

import com.framework.loippi.entity.activity.ShopActivity;
import lombok.Data;

import java.util.Date;

/**
 * Created by longbh on 2018/12/16.
 */
@Data
public class ActivityDetailResult {

    //活动id
    private Long activityId;
    //分类id
    private Long classId;
    //分类名称
    private String className;
    //促销规则id
    private Long ruleId;
    //促销规则名称
    private String ruleName;
    //活动信息
    private String activityName;
    private String activitySubTitle;
    private String activityTitle;
    //活动类别
    private Integer activityType;
    // 活动状态   10即将开始   20已开始
    private Integer activityStatus;
    //活动时间
    private Date startTime;
    private Date endTime;

    public static ActivityDetailResult build(ShopActivity shopActivity) {
        ActivityDetailResult activityDetailResult = new ActivityDetailResult();
        activityDetailResult.setActivityId(shopActivity.getId());
        activityDetailResult.setActivityName(shopActivity.getName());
        activityDetailResult.setActivitySubTitle(shopActivity.getSubActivityName());
        activityDetailResult.setActivityTitle(shopActivity.getActivityName());
        activityDetailResult.setClassId(shopActivity.getActivityClassId());
        activityDetailResult.setRuleId(shopActivity.getPromotionRuleId());
        activityDetailResult.setActivityType(shopActivity.getActivityType());
        activityDetailResult.setActivityStatus(shopActivity.getActivityStatus());
        activityDetailResult.setStartTime(shopActivity.getStartTime());
        activityDetailResult.setEndTime(shopActivity.getEndTime());
        //activityDetailResult.setRuleName(shopActivity.getPromotionType());
        return activityDetailResult;
    }

}
