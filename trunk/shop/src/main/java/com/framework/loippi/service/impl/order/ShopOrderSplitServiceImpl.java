package com.framework.loippi.service.impl.order;

import com.framework.loippi.entity.order.ShopOrderSplit;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.order.ShopOrderSplitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
