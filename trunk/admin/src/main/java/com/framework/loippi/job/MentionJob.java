package com.framework.loippi.job;

import com.framework.loippi.service.ware.RdMentionCalculateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 定时任务统计自提店信息
 */
@Service
@EnableScheduling
@Lazy(false)
public class MentionJob {

    private static final Logger log = LoggerFactory.getLogger(ReportJob.class);
    @Resource
    private RdMentionCalculateService rdMentionCalculateService;

    /**
     * 定时任务统计自提店上一个月销售额信息以及计算补贴系数补贴金额
     */
    //@Scheduled(cron = "0/30 * * * * ? ")  //每5秒执行一次
    @Scheduled(cron = "0 10 0 4 * ? " )  //每月4号凌晨0点10分初始化当月的财务月报表
    public void getDailyOrderCensus() {
        log.info("#################################################################");
        log.info("############  定时任务执行统计前一月自提店销售额信息 ##############");
        log.info("#################################################################");
        rdMentionCalculateService.statistical();
    }
}
