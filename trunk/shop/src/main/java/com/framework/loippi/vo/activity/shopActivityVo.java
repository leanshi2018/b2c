package com.framework.loippi.vo.activity;

import com.framework.loippi.entity.activity.ShopActivity;
import lombok.Data;

/**
 * Created by Administrator on 2017/8/12.
 */
@Data
public class shopActivityVo {


    /**
     * 活动
     */
    private ShopActivity  shopActivity;


    /**
     * 活动状态    1活动中    2活动未开始但已开启  3活动关闭
     */
    private  Integer  activityState;


}
