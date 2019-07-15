package com.framework.loippi.dao.product;

import com.framework.loippi.entity.product.ShopGoodsBrand;
import com.framework.loippi.mybatis.dao.GenericDao;

import java.util.List;

/**
 * DAO - ShopGoodsBrand(品牌表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopGoodsBrandDao  extends GenericDao<ShopGoodsBrand, Long> {
    List<ShopGoodsBrand> findByClassId(Long classId);
    List<ShopGoodsBrand> findByClassKey(String keyword);
    List<ShopGoodsBrand> findByTpypeId(Long typeId );



}
