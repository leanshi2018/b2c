package com.framework.loippi.dao.product;

import com.framework.loippi.entity.product.ShopGoodsSpecIndex;
import com.framework.loippi.mybatis.dao.GenericDao;
import org.apache.ibatis.annotations.Param;

/**
 * DAO - ShopGoodsSpecIndex(商品与规格对应表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopGoodsSpecIndexDao  extends GenericDao<ShopGoodsSpecIndex, Long> {

    void deleteByTypeId(@Param("typeId") Long typeId);

}
