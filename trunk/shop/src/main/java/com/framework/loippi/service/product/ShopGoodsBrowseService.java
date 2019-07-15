package com.framework.loippi.service.product;

import com.framework.loippi.entity.product.ShopGoodsBrowse;
import com.framework.loippi.service.GenericService;

import java.util.Map;

/**
 * SERVICE - ShopReturnReason()
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopGoodsBrowseService extends GenericService<ShopGoodsBrowse, Long> {
    /**
     * 根据条件删除足迹
     * @param var1
     * @return
     */
    Long deleteMemberFavorites(Map<String, Object> var1);
}
