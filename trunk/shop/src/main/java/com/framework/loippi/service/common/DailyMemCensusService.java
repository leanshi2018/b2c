package com.framework.loippi.service.common;

import com.framework.loippi.entity.common.DailyMemCensus;
import com.framework.loippi.service.GenericService;

/**
 * 日会员统计信息
 */
public interface DailyMemCensusService extends GenericService<DailyMemCensus, Long> {
    void getDailyMemCensus();

    void getExcelByTime(String s, String s1);
}
