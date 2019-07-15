package com.framework.loippi.dao.walet;

import com.framework.loippi.entity.walet.ShopWalletLog;
import com.framework.loippi.mybatis.dao.GenericDao;

import com.framework.loippi.vo.stats.StatsCountVo;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * DAO - ShopWalletLog(预存款变更日志表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopWalletLogDao  extends GenericDao<ShopWalletLog, Long> {

    /**
     * 财务管理-统计总支出
     * @return
     */
    BigDecimal countAmount(ShopWalletLog param);
    BigDecimal countAmountStore(ShopWalletLog param);
    BigDecimal countAmountParams(Map<String, Object> params);

    /**
     * 统计昨日 前日 上周 上上周, 上月, 上上月总收入
     */
    List<StatsCountVo> listStatsCountVoByIncome();

    /**
     * 统计昨日 前日 上周 上上周, 上月, 上上月总收入
     */
    List<StatsCountVo> listStatsCountVoByExpenditure();


 /**
     * 批量插入
     */
    void insertBatch(List<ShopWalletLog> walletLogList);
}
