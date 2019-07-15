package com.framework.loippi.service.product;

import com.framework.loippi.entity.product.ShopGoodsTypeBrand;
import com.framework.loippi.service.GenericService;

import java.util.List;

/**
 * SERVICE - ShopGoodsTypeBrand(商品类型与品牌对应表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopGoodsTypeBrandService  extends GenericService<ShopGoodsTypeBrand, Long> {
    void batchSave(List<ShopGoodsTypeBrand> list);
    List<ShopGoodsTypeBrand> findByTypes(Long typeID);

}
