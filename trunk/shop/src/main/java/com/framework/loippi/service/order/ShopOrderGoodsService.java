package com.framework.loippi.service.order;

import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.service.GenericService;

/**
 * SERVICE - ShopOrderGoods(订单商品表)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopOrderGoodsService extends GenericService<ShopOrderGoods, Long> {

    /**
     * @param orderId          订单编号
     * @param evaluationStatus 0:未评价 1:已评价
     */
    List<ShopOrderGoods> findEvalListBy(Long orderId, Integer evaluationStatus);

    //根据订单获取商品列表
    Map<Long, List<ShopOrderGoods>> findOrderMap(List<Long> ids);

    Long updateBatch(ShopOrderGoods shopOrderGoods);
    /**
     * 批量插入
     */
    void insertBatch(List<ShopOrderGoods> shopOrderGoodsList);

    /**
     * 删除
     * @param shopOrderGoods
     */
    void deleteByEntity(ShopOrderGoods shopOrderGoods);

    void updateBatchForShipmentNum(List<ShopOrderGoods> shopOrderGoodsList);

    List<ShopOrderGoods> listByOrderId(Long id);
}
