package com.framework.loippi.service.activity;

import com.framework.loippi.entity.activity.ShopActivityGoodsSpec;
import com.framework.loippi.service.GenericService;

import java.util.List;
import java.util.Map;


/**
 * SERVICE - ShopActivityGoodsSpec(活动规格)
 *
 * @author longbh
 * @version 2.0
 */
public interface ShopActivityGoodsSpecService extends GenericService<ShopActivityGoodsSpec, Long> {

    //id查询批量规格
    Map<Long, List<ShopActivityGoodsSpec>> findMapSpecList(List<Long> idList);

    //批量查询回商品相关活动规格
    Map<Long, ShopActivityGoodsSpec> findByAtiGoodsId(Long goodsId);

    //查询某活动对应商品的所有规格
    Map<Long, ShopActivityGoodsSpec> findByIds(List<Long> atiGoodsId);

}
