package com.framework.loippi.dao.product;

import com.framework.loippi.entity.product.ShopGoodsTypeBrand;
import com.framework.loippi.mybatis.dao.GenericDao;

import java.util.List;

/**
 * DAO - ShopGoodsTypeBrand(商品类型与品牌对应表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopGoodsTypeBrandDao  extends GenericDao<ShopGoodsTypeBrand, Long> {
    void batchSave(List<ShopGoodsTypeBrand> list);
    public List<ShopGoodsTypeBrand> findByTypes(Long typeID);
    void  deleteByTypeId(Long typeID);
}
