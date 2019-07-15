package com.framework.loippi.dao.order;

import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.user.ShopMemberFavorites;
import com.framework.loippi.entity.walet.ShopWalletLog;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.utils.Paramap;

import java.util.List;
import java.util.Map;

/**
 * DAO - ShopOrderGoods(订单商品表)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopOrderGoodsDao extends GenericDao<ShopOrderGoods, Long> {

    //查询多订单订单项
    List<ShopOrderGoods> listByOrderIds(List<Long> orderIds);

    //评论过的商品
    List<ShopOrderGoods> findEvalGoodsByEvalStatusAndOrderId(Paramap orderGoodsParams);

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

}
