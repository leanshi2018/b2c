package com.framework.loippi.service.impl.order;

import com.framework.loippi.entity.order.OrderFundFlow;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.order.OrderFundFlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class OrderFundFlowServiceImpl extends GenericServiceImpl<OrderFundFlow, Long> implements OrderFundFlowService {
}
