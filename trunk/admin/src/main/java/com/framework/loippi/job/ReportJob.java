package com.framework.loippi.job;

import com.framework.loippi.entity.common.DailyOrderCensus;
import com.framework.loippi.service.common.DailyOrderCensusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


/**
 * 定时任务统计相关报表信息
 */
@Service
@EnableScheduling
@Lazy(false)
public class ReportJob {
    @Resource
    private DailyOrderCensusService dailyOrderCensusService;


    private static final Logger log = LoggerFactory.getLogger(ReportJob.class);

    private static String secretkey = "1073f238-1971-4890-bfc4-fd903d90d7eb10294";//秘钥
    private static String customerID = "10294";//客户编号

    /**
     * 定时任务统计前一日会员订单信息数据
     */
    //@Scheduled(cron = "0/5 * * * * ? ")  //每5秒执行一次
    @Scheduled(cron = "0 5 0 * * ? " )  //每天0点5分执行一次
    public void getDailyOrderCensus() {
        log.info("#################################################################");
        log.info("##############  定时任务执行统计前一日会员订单信息 ################");
        log.info("#################################################################");
        dailyOrderCensusService.getDailyOrderCensus();
    }
}