package com.framework.loippi.dao.activity;

import com.framework.loippi.entity.activity.ShopActivityGoodsSpec;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * DAO - ShopActivityGoodsSpec(活动规格)
 * 
 * @author longbh
 * @version 2.0
 */
public interface ShopActivityGoodsSpecDao  extends GenericDao<ShopActivityGoodsSpec, Long> {

    void deleteByGoodsId(Long goodsId);

    void deleteShopActivityGoodsSpecByActivityId(Long activityId);

    void updateBatchSpec(ShopActivityGoodsSpec shopActivityGoodsSpec);


}
