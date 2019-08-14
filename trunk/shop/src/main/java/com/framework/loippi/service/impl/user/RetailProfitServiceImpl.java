package com.framework.loippi.service.impl.user;

import com.framework.loippi.utils.Paramap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.dao.user.RetailProfitDao;
import com.framework.loippi.entity.user.RetailProfit;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RetailProfitService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    public BigDecimal findPeriodPay(Paramap put) {
        return retailProfitDao.findPeriodPay(put);
    }

    @Override
    public BigDecimal findPeriodNoPay(Paramap put) {
        return retailProfitDao.findPeriodNoPay(put);
    }


}
