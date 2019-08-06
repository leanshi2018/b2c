package com.framework.loippi.service.impl.user;

import com.framework.loippi.dao.order.ShopOrderLogisticsDao;
import com.framework.loippi.dao.user.RetailProfitDao;
import com.framework.loippi.entity.user.RetailProfit;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RetailProfitService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

public class RetailProfitServiceImpl extends GenericServiceImpl<RetailProfit, Long> implements RetailProfitService {
    @Resource
    private RetailProfitDao retailProfitDao;
}
