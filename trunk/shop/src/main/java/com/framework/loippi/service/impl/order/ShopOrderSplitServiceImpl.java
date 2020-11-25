package com.framework.loippi.service.impl.order;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.dao.order.ShopOrderSplitDao;
import com.framework.loippi.entity.order.ShopOrderSplit;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.order.ShopOrderSplitService;

/**
 * SERVICE - ShopOrder(订单表)
 *
 * @author zijing
 * @version 2.0
 */
@Service
@Slf4j
@Transactional
public class ShopOrderSplitServiceImpl extends GenericServiceImpl<ShopOrderSplit, Long> implements ShopOrderSplitService{
    @Autowired
    private TwiterIdService twiterIdService;
    @Autowired
    private ShopOrderSplitDao shopOrderSplitDao;

    @Override
    public BigDecimal findSplitPpv(String mmCode, String period) {
        Map<String,Object> map = new HashMap<>();
        map.put("periodCode",period);
        map.put("mmCode",mmCode);
        return shopOrderSplitDao.findSplitPpv(map);
    }
}
