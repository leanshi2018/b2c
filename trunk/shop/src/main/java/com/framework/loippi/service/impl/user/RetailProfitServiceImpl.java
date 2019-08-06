package com.framework.loippi.service.impl.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.dao.user.RetailProfitDao;
import com.framework.loippi.entity.user.RetailProfit;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RetailProfitService;
@Service
@Transactional
public class RetailProfitServiceImpl extends GenericServiceImpl<RetailProfit, Long> implements RetailProfitService {
    @Autowired
    private RetailProfitDao retailProfitDao;
}
