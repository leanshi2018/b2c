package com.framework.loippi.service.activity;

import com.framework.loippi.entity.activity.ShopActivityGoods;
import com.framework.loippi.entity.activity.ShopActivity;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.result.common.goods.GoodsListResult;
import com.framework.loippi.service.GenericService;

import java.util.List;
import java.util.Map;

/**
 * SERVICE - NShopActivityGoods(活动商品规格)
 *
 * @author longbh
 * @version 2.0
 */
public interface ShopActivityGoodsService extends GenericService<ShopActivityGoods, Long> {

    //填充商品活动信息
    List<GoodsListResult> findAndAddAtiInfo(List<ShopGoods> shopGoodsList, String prefix);

    //审核状态
    void updateStatus(Long id, Integer status);

    void saveGoodSpec(Long storeId, ShopActivity shopActivity, Long goodsId, Long id, String specJson, Integer maxOrderBuy, Integer maxUserBuy, String activityPicture, Long screeningsId);

    Map<Long, List<ShopActivityGoods>> findGoodsByActivityMap(List<Long> activityIds);
    //剔除已参加活动的商品
     List<Long> getSpecIds(String goodSpecIds);
     //
     void deleteActivityGoods(Long[] ids);
}
