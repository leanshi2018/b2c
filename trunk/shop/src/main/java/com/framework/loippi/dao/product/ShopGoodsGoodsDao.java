package com.framework.loippi.dao.product;

import com.framework.loippi.entity.product.ShopGoodsGoods;
import com.framework.loippi.mybatis.dao.GenericDao;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * DAO - ShopGoodsGoods(组合商品，商品选择)
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopGoodsGoodsDao  extends GenericDao<ShopGoodsGoods, Long> {

    void deleteNotIds(@Param("ids") List<Long> ids);

}
