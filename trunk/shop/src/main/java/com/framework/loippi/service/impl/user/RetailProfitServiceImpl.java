package com.framework.loippi.service.impl.user;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import com.framework.loippi.utils.Paramap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.dao.user.RetailProfitDao;
import com.framework.loippi.entity.user.RetailProfit;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RetailProfitService;
import com.framework.loippi.utils.Paramap;
@Service
@Transactional
public class RetailProfitServiceImpl extends GenericServiceImpl<RetailProfit, Long> implements RetailProfitService {
    @Autowired
    private RetailProfitDao retailProfitDao;

    @Override
    public List<RetailProfit> findNoGrantByCode(String mmCode) {
        return retailProfitDao.findNoGrantByCode(mmCode);
    }

    @Override
    public BigDecimal findTotalProfit(HashMap<String, Object> map) {
        return retailProfitDao.findTotalProfit(map);
    }

    @Override
    public BigDecimal countProfit(Paramap put) {
        return retailProfitDao.countProfit(put);
    }
    @Override
    public BigDecimal findPeriodPay(Paramap put) {
        return retailProfitDao.findPeriodPay(put);
    }

    @Override
    public BigDecimal findPeriodNoPay(Paramap put) {
        return retailProfitDao.findPeriodNoPay(put);
    }


}
