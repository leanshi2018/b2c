package com.framework.loippi.service.impl.order;

import com.framework.loippi.consts.OrderState;
import com.framework.loippi.dao.order.ShopOrderDao;
import com.framework.loippi.dao.order.ShopOrderGoodsDao;
import com.framework.loippi.dao.product.ShopGoodsDao;
import com.framework.loippi.entity.order.ShopOrder;
import com.framework.loippi.entity.order.ShopOrderGoods;
import com.framework.loippi.entity.product.ShopGoods;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.order.ShopOrderGoodsService;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SERVICE - ShopOrderGoods(订单商品表)
 *
 * @author zijing
 * @version 2.0
 */
@Service
public class ShopOrderGoodsServiceImpl extends GenericServiceImpl<ShopOrderGoods, Long>
        implements ShopOrderGoodsService {

    @Autowired
    private ShopOrderGoodsDao shopOrderGoodsDao;

    @Autowired
    private ShopOrderDao shopOrderDao;

    @Autowired
    private ShopGoodsDao shopGoodsDao;

    @Autowired
    public void setGenericDao() {
        super.setGenericDao(shopOrderGoodsDao);
    }

    @Override
    public List<ShopOrderGoods> findEvalListBy(Long orderId, Integer evaluationStatus) {
        Paramap paramap = Paramap.create();
        paramap.put("id", orderId);
        paramap.put("orderState", OrderState.ORDER_STATE_FINISH);
        List<ShopOrder> shopOrderList = shopOrderDao.findByParams(paramap);
        if (shopOrderList == null || shopOrderList.size() != 1) {
            throw new RuntimeException("不存在订单");
        }

        Paramap orderGoodsParams = Paramap.create();
        orderGoodsParams.put("orderId", shopOrderList.get(0).getId());
        // orderGoodsParams.put("evaluationStatus", evaluationStatus);
        return shopOrderGoodsDao.findEvalGoodsByEvalStatusAndOrderId(orderGoodsParams);
    }

    //根据订单获取商品列表
    @Override
    public Map<Long, List<ShopOrderGoods>> findOrderMap(List<Long> ids) {
        List<ShopOrderGoods> shopOrderGoodses = shopOrderGoodsDao.listByOrderIds(ids);
        Map<Long, List<ShopOrderGoods>> orderMap = new HashMap<>();
        for (ShopOrderGoods shopOrderGoods : shopOrderGoodses) {
            List<ShopOrderGoods> orderGoodses = orderMap.get(shopOrderGoods.getOrderId());
            if (orderGoodses == null) {
                orderGoodses = new ArrayList<>();
            }
            orderGoodses.add(shopOrderGoods);
            orderMap.put(shopOrderGoods.getOrderId(), orderGoodses);
        }
        return orderMap;
    }

    @Override
    public Long updateBatch(ShopOrderGoods shopOrderGoods) {

        return shopOrderGoodsDao.updateBatch(shopOrderGoods);
    }

    @Override
    public void insertBatch(List<ShopOrderGoods> shopOrderGoodsList) {
        shopOrderGoodsDao.insertBatch(shopOrderGoodsList);
    }

    @Override
    public void deleteByEntity(ShopOrderGoods shopOrderGoods) {
        shopOrderGoodsDao.deleteByEntity(shopOrderGoods);
    }

    @Override
    public void updateBatchForShipmentNum(List<ShopOrderGoods> shopOrderGoodsList) {
        shopOrderGoodsDao.updateBatchForShipmentNum(shopOrderGoodsList);
    }

}
