package com.framework.loippi.service.product;

import com.framework.loippi.entity.product.ShopGoodsSpecIndex;
import com.framework.loippi.service.GenericService;

/**
 * SERVICE - ShopGoodsSpecIndex(商品与规格对应表)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopGoodsSpecIndexService extends GenericService<ShopGoodsSpecIndex, Long> {

    void deleteByTypeId(Long id);

}
