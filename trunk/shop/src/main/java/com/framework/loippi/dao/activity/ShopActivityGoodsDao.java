package com.framework.loippi.dao.activity;

import com.framework.loippi.entity.activity.ShopActivityGoods;
import com.framework.loippi.mybatis.dao.GenericDao;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;
import java.util.Map;

/**
 * DAO - ShopActivityGoods(活动商品规格)
 *
 * @author longbh
 * @version 2.0
 */
public interface ShopActivityGoodsDao extends GenericDao<ShopActivityGoods, Long> {

    void deleteShopActivityGoodsByActivityId(Long activityId);

    /**
     * 查找参加了活动的商品
     * @param var1
     * @return
     */
    List<ShopActivityGoods> findActivityGoods(Map<String, Object> var1);

}
