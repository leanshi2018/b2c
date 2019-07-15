package com.framework.loippi.service.product;

import com.framework.loippi.entity.product.ShopGoodsTypeSpec;
import com.framework.loippi.service.GenericService;

import java.util.List;
import java.util.Map;

/**
 * SERVICE - ShopGoodsTypeSpec(商品类型与规格对应表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopGoodsTypeSpecService  extends GenericService<ShopGoodsTypeSpec, Long> {
    void batchSave(List<ShopGoodsTypeSpec> list);
    List<ShopGoodsTypeSpec> findByTypes(Long typeID);
    public void deleteByTypeIds(Long[] ids);

    public void deleteByMap(Map<String, Object> params);


}
