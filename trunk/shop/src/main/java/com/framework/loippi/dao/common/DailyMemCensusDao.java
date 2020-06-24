package com.framework.loippi.dao.common;

import com.framework.loippi.entity.common.DailyMemCensus;
import com.framework.loippi.entity.common.DailyOrderCensus;
import com.framework.loippi.entity.common.MemberShippingBehavior;
import com.framework.loippi.mybatis.dao.GenericDao;

import java.util.HashMap;
import java.util.List;

/**
 * 日会员信息统计记录
 */
public interface DailyMemCensusDao extends GenericDao<DailyMemCensus, Long> {
    List<DailyMemCensus> findByTime(HashMap<String, Object> map);
}
