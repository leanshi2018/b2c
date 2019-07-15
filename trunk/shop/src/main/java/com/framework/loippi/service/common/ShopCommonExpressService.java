package com.framework.loippi.service.common;

import com.framework.loippi.entity.common.ShopCommonExpress;
import com.framework.loippi.service.GenericService;

/**
 * SERVICE - ShopCommonExpress(快递公司)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopCommonExpressService extends GenericService<ShopCommonExpress, Long> {

    public ShopCommonExpress findByOrderId(Long orderId);

}
