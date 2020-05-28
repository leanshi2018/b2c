package com.framework.loippi.job;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import com.framework.loippi.service.common.DailyMemCensusService;
import com.framework.loippi.service.common.DailyOrderCensusService;
import com.framework.loippi.service.common.MemberIndicatorService;
import com.framework.loippi.service.common.MemberShippingBehaviorService;


/**
 * 定时任务统计相关报表信息
 */
@Service
@EnableScheduling
@Lazy(false)
public class ReportJob {
    @Resource
    private DailyOrderCensusService dailyOrderCensusService;
    @Resource
    private DailyMemCensusService dailyMemCensusService;
    @Resource
    private MemberIndicatorService memberIndicatorService;
    @Resource
    private MemberShippingBehaviorService memberShippingBehaviorService;

    private static final Logger log = LoggerFactory.getLogger(ReportJob.class);

    private static String secretkey = "1073f238-1971-4890-bfc4-fd903d90d7eb10294";//秘钥
    private static String customerID = "10294";//客户编号

    /**
     * 定时任务统计前一日会员订单信息数据
     */
    //@Scheduled(cron = "0/5 * * * * ? ")  //每5秒执行一次
    //@Scheduled(cron = "0 5 0 * * ? " )  //每天0点5分执行一次
    public void getDailyOrderCensus() {
        log.info("#################################################################");
        log.info("##############  定时任务执行统计前一日会员订单信息 ################");
        log.info("#################################################################");
        dailyOrderCensusService.getDailyOrderCensus();
    }

    /**
     * 定时任务统计每日会员数据
     */
    //@Scheduled(cron = "0/5 * * * * ? ")  //每5秒执行一次
    //@Scheduled(cron = "23 55 0 * * ? " )  //每天0点5分执行一次
    public void getDailyMemCensus() {
        log.info("#################################################################");
        log.info("##################  定时任务统计每日会员数据   ####################");
        log.info("#################################################################");
        dailyMemCensusService.getDailyMemCensus();
    }

    /**
     * 按周期统计会员指标
     */
    //@Scheduled(cron = "0/15 * * * * ? ")  //每5秒执行一次
    //@Scheduled(cron = "23 55 0 * * ? " )  //每天0点5分执行一次
    public void getMemberIndicator() {
        log.info("#################################################################");
        log.info("##################  定时任务统计周期会员指标   ####################");
        log.info("#################################################################");
        memberIndicatorService.getMemberIndicator();
    }
    /**
     * 每日进行新老会员购买行为统计
     */
    //@Scheduled(cron = "0/5 * * * * ? ")  //每5秒执行一次
    //@Scheduled(cron = "23 55 0 * * ? " )  //每天0点5分执行一次
    public void getMemberShippingBehavior() {
        log.info("#################################################################");
        log.info("###############  定时任务新老会员购买行为统计   ###################");
        log.info("#################################################################");
        memberShippingBehaviorService.getMemberShippingBehavior();
    }
}