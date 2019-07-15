package com.framework.loippi.vo.activity;

import lombok.Data;

import java.util.List;

/**
 * 促销活动  分组商品列表
 */
@Data
public class ActivityZhuanChangGroupsVo {

    //分组名称
    private String groupName;

    //分组id
    private Long groupId;

    //专场商品列表
    private List<ActivityZhuanChangGoodsVo> activityList;

}
