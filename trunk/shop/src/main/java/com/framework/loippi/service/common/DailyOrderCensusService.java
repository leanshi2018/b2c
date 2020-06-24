package com.framework.loippi.service.common;

import com.framework.loippi.entity.common.DailyOrderCensus;
import com.framework.loippi.service.GenericService;

/**
 * 日会员订单信息
 */
public interface DailyOrderCensusService extends GenericService<DailyOrderCensus, Long> {
    void getDailyOrderCensus();

    void getExcelByTime(String s, String s1);
}
