package com.framework.loippi.job;

import com.framework.loippi.consts.ActivityStatus;
import com.framework.loippi.entity.activity.ShopActivity;
import com.framework.loippi.service.activity.ShopActivityService;

import java.util.Date;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Created by longbh on 2018/10/30.
 */
@Component
@EnableScheduling
@Lazy(false)
public class ShopActivityStatusJob {

    @Autowired
    private ShopActivityService shopActivityService;

    /**
     * 活动状态修改器
     */
    @Scheduled(cron = "0 */1 * * * ?")  //每5分钟执行一次
    public synchronized void scheduleStatus() {
        Date todayTime = new Date();
        List<ShopActivity> activityList = shopActivityService.findErrorStatusData();
        for (ShopActivity activity : activityList) {
            int status = -1;//活动状态  10  未开始  20 活动中 30已结束
            //活动处于时间范围内，并是开启状态  为活动中
            if (activity.getStartTime().before(todayTime) && activity.getEndTime().after(todayTime)) {
                status = ActivityStatus.ACTIVITY_STATUS_NOW;
            } else if (activity.getStartTime().before(todayTime) && activity.getEndTime().after(todayTime)) {////活动处于时间范围内，并是不开启状态  为未开始
                status = ActivityStatus.ACTIVITY_STATUS_FUTURE;
            } else if (activity.getEndTime().before(todayTime)) {
                status = ActivityStatus.ACTIVITY_STATUS_PAST;
            } else if (activity.getStartTime().after(todayTime)) {
                status = ActivityStatus.ACTIVITY_STATUS_FUTURE;
            } else {
                status = -1;
            }
            if (status > 0 && (activity.getActivityStatus() == null || activity.getActivityStatus() != status)) {
                activity.setActivityStatus(status);
                shopActivityService.update(activity);
            }
        }
    }

}
