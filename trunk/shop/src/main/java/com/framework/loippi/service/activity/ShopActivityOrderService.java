package com.framework.loippi.service.activity;


import com.framework.loippi.entity.activity.ShopActivityOrder;
import com.framework.loippi.support.Pageable;

import java.util.List;

/**
 * 订单优惠表Service接口
 *
 * @author kwg
 * @version 2016-09-18
 */
public interface ShopActivityOrderService {

	/**
	 * 查询分页订单优惠表数据
	 * 
	 * @param
	 * @return
	 */
	public List<ShopActivityOrder> findShopActivityOrderPagerList(Pageable pageable);

	/**
	 * 通过id获取单条订单优惠表数据
	 * 
	 * @param
	 * @return
	 */
	public List<ShopActivityOrder> findShopActivityOrderByOrderId(Long orderId);

	/**
	 * 通过id删除订单优惠表数据
	 * 
	 * @param id
	 */
	public void deleteShopActivityOrderById(Long id);

	/**
	 * 修改订单优惠表数据
	 * 
	 * @param shopActivityOrder
	 */
	public void updateShopActivityOrder(ShopActivityOrder shopActivityOrder);

	/**
	 * 保存订单优惠表数据
	 * 
	 * @param shopActivityOrder
	 */
	public void saveShopActivityOrder(ShopActivityOrder shopActivityOrder);

	/**
	 * 获取所有订单优惠表数据
	 * 
	 * @return
	 */
	public List<ShopActivityOrder> findShopActivityOrderAllList();
	
}