package com.framework.loippi.dao.user;

import com.framework.loippi.entity.user.RetailProfit;
import com.framework.loippi.mybatis.dao.GenericDao;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

public interface RetailProfitDao extends GenericDao<RetailProfit, Long> {
    /**
     * 根据当前时间查询已经达到发放时间且状态未发放的零售利润记录
     * @param expectTime
     * @return
     */
    List<RetailProfit> findTimeMature(String expectTime);

    /**
     * 根据当前会员查询延期发放的零售利润记录
     * @param mmCode
     * @return
     */
    List<RetailProfit> findNoGrantByCode(String mmCode);

    BigDecimal findTotalProfit(HashMap<String, Object> map);
}
