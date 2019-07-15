package com.framework.loippi.service.impl.walet;

import com.framework.loippi.consts.StatsConsts;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.vo.stats.StatsCountVo;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.walet.ShopWalletLogDao;
import com.framework.loippi.entity.walet.ShopWalletLog;
import com.framework.loippi.service.wallet.ShopWalletLogService;

import java.math.BigDecimal;
import java.util.Map;

/**
 * SERVICE - ShopWalletLog(预存款变更日志表)
 *
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopWalletLogServiceImpl extends GenericServiceImpl<ShopWalletLog, Long> implements ShopWalletLogService {

    @Autowired
    private ShopWalletLogDao shopWalletLogDao;


    @Autowired
    public void setGenericDao() {
        super.setGenericDao(shopWalletLogDao);
    }

    /**
     * 财务管理-统计总支出
     */
    public BigDecimal countAmount(ShopWalletLog param) {
        return shopWalletLogDao.countAmount(param);
    }

    public BigDecimal countAmountStore(ShopWalletLog param) {
        return shopWalletLogDao.countAmountStore(param);
    }

    @Override
    public BigDecimal countAmountParams(Map<String, Object> params) {
        return shopWalletLogDao.countAmountParams(params);
    }

    @Override
    public List<StatsCountVo> listStatsCountVo(int incomeOrExpenditure) {
        if (incomeOrExpenditure == StatsConsts.STATS_TYPE_TRADE_INCOME) {
            return shopWalletLogDao.listStatsCountVoByIncome();
        } else if (incomeOrExpenditure == StatsConsts.STATS_TYPE_TRADE_EXPENDITURE) {
            return shopWalletLogDao.listStatsCountVoByExpenditure();
        } else {
            throw new IllegalStateException();
        }
    }

	/**
	 * 批量插入
	 */
	public void insertBatch(List<ShopWalletLog> walletLogList){
		shopWalletLogDao.insertBatch(walletLogList);
	}
}
