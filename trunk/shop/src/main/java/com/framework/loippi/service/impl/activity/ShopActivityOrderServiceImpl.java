package com.framework.loippi.service.impl.activity;


import com.framework.loippi.dao.activity.ShopActivityOrderDao;
import com.framework.loippi.entity.activity.ShopActivityOrder;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.activity.ShopActivityOrderService;
import com.framework.loippi.support.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 订单优惠表ServiceImpl
 *
 * @author kwg
 * @version 2016-09-18
 */
@Service
public class ShopActivityOrderServiceImpl implements ShopActivityOrderService {

    //活动订单表
    @Autowired
    private ShopActivityOrderDao shopActivityOrderDao;
    @Autowired
    private TwiterIdService twiterIdService;

    /**
     * 查询分页订单优惠表数据
     *
     * @param pager 分页对象
     * @return
     */
    @Override
    public List<ShopActivityOrder> findShopActivityOrderPagerList(Pageable pager) {
        return shopActivityOrderDao.findShopActivityOrderPagerList(pager);
    }

    /**
     * 通过id获取单条订单优惠表数据
     *
     * @param orderId
     * @return
     */
    @Override
    public List<ShopActivityOrder> findShopActivityOrderByOrderId(Long orderId) {
        return shopActivityOrderDao.findShopActivityOrderByOrderId(orderId);
    }

    /**
     * 通过id删除订单优惠表数据
     *
     * @param id
     */
    @Override
    public void deleteShopActivityOrderById(Long id) {
        shopActivityOrderDao.deleteShopActivityOrderById(id);
    }

    /**
     * 修改订单优惠表数据
     *
     * @param shopActivityOrder
     */
    @Override
    public void updateShopActivityOrder(ShopActivityOrder shopActivityOrder) {
        shopActivityOrder.setUpdateTime(new Date());
        shopActivityOrderDao.updateShopActivityOrder(shopActivityOrder);
    }

    /**
     * 保存订单优惠表数据
     *
     * @param shopActivityOrder
     */
    @Override
    public void saveShopActivityOrder(ShopActivityOrder shopActivityOrder) {
        shopActivityOrder.setId(twiterIdService.getTwiterId());
        shopActivityOrder.setCreateTime(new Date());
        shopActivityOrderDao.saveShopActivityOrder(shopActivityOrder);
    }

    /**
     * 获取所有订单优惠表数据
     *
     * @return
     */
    @Override
    public List<ShopActivityOrder> findShopActivityOrderAllList() {
        return shopActivityOrderDao.findShopActivityOrderAllList();
    }

}