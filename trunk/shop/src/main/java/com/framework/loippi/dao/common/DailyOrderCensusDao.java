package com.framework.loippi.dao.common;

import com.framework.loippi.entity.common.DailyOrderCensus;
import com.framework.loippi.mybatis.dao.GenericDao;

import java.util.HashMap;
import java.util.List;

/**
 * 日会员订单信息统计记录
 */
public interface DailyOrderCensusDao extends GenericDao<DailyOrderCensus, Long> {
    List<DailyOrderCensus> findByTime(HashMap<String, Object> map);
}
