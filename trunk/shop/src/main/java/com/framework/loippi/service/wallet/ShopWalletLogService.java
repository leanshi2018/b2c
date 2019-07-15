package com.framework.loippi.service.wallet;

import com.framework.loippi.entity.walet.ShopWalletLog;
import com.framework.loippi.service.GenericService;

import com.framework.loippi.vo.stats.StatsCountVo;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * SERVICE - ShopWalletLog(预存款变更日志表)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopWalletLogService extends GenericService<ShopWalletLog, Long> {

    /**
     * 财务管理-统计总支出
     * @return
     */
    BigDecimal countAmount(ShopWalletLog param);
    BigDecimal countAmountStore(ShopWalletLog param);
    BigDecimal countAmountParams(Map<String, Object> params);

    /**
     * 统计-昨日 前日 上周 上上周, 上月, 上上月 总收入/总支出
     */
    List<StatsCountVo> listStatsCountVo(int incomeOrExpenditure);

    /**
     * 批量插入
     */
    void insertBatch(List<ShopWalletLog> walletLogList);

}
