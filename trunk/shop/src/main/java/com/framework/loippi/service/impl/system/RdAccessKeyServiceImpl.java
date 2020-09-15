package com.framework.loippi.service.impl.system;

import com.framework.loippi.entity.cart.ShopCartExchange;
import com.framework.loippi.entity.system.RdAccessKey;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.product.ShopCartExchangeService;
import com.framework.loippi.service.system.RdAccessKeyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class RdAccessKeyServiceImpl extends GenericServiceImpl<RdAccessKey, Long> implements RdAccessKeyService {
}
