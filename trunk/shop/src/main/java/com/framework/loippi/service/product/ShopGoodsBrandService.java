package com.framework.loippi.service.product;

import com.framework.loippi.entity.product.ShopGoodsBrand;
import com.framework.loippi.service.GenericService;

import java.util.List;
import java.util.Map;

/**
 * 品牌
 */
public interface ShopGoodsBrandService extends GenericService<ShopGoodsBrand, Long> {

    /**
     * 根据classId查询品牌
     */
    List<ShopGoodsBrand> findByClassId(Long classId);


    List<ShopGoodsBrand> findByClassKey(String keyword);

    //转map
    Map<Long, ShopGoodsBrand> findToMap(List<Long> ids);
}
