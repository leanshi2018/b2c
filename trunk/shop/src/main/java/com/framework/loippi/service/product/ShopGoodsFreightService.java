package com.framework.loippi.service.product;


import com.framework.loippi.entity.product.ShopGoodsFreight;
import com.framework.loippi.service.GenericService;

import java.math.BigDecimal;

/**
 * SERVICE - ShopGoodsFreight(地区运费表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopGoodsFreightService  extends GenericService<ShopGoodsFreight, Long> {

    /**
     * 计算运费
     * @param areaId  地区id
     * @param totalWeight 商品总重量
     * @return
     */
    BigDecimal CalculateFreight(String areaId, Double totalWeight);
}
