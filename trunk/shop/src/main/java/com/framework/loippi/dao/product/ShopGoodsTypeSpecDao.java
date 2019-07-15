package com.framework.loippi.dao.product;

import com.framework.loippi.entity.product.ShopGoodsTypeBrand;
import com.framework.loippi.entity.product.ShopGoodsTypeSpec;
import com.framework.loippi.mybatis.dao.GenericDao;

import java.util.List;
import java.util.Map;

/**
 * DAO - ShopGoodsTypeSpec(商品类型与规格对应表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopGoodsTypeSpecDao  extends GenericDao<ShopGoodsTypeSpec, Long> {
    void batchSave(List<ShopGoodsTypeSpec> list);
    public List<ShopGoodsTypeSpec> findByTypes(Long typeID);
    void deleteByTypeId(Long typeID);

    public void deleteByTypeIds(Map<String,Object> map);

    public void deleteByMap(Map<String,Object> map);

}
