package com.framework.loippi.dao.activity;


import com.framework.loippi.entity.activity.ShopActivityOrder;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.support.Pageable;

import java.util.List;

/**
 * 订单优惠表DAO接口
 *
 * @author kwg
 * @version 2016-09-18
 */
public interface ShopActivityOrderDao  extends GenericDao<ShopActivityOrder, Long> {

	/**
	 * 查询分页订单优惠表数据
	 * 
	 * @param pageable 分页对象
	 * @return
	 */
	List<ShopActivityOrder> findShopActivityOrderPagerList(Pageable pageable);

	/**
	 * 通过id获取单条订单优惠表数据
	 * 
	 * @param id
	 * @return
	 */
	List<ShopActivityOrder> findShopActivityOrderByOrderId(Long id);

	/**
	 * 通过id删除订单优惠表数据
	 * 
	 * @param id
	 */
	void deleteShopActivityOrderById(Long id);

	/**
	 * 修改订单优惠表数据
	 * 
	 * @param shopActivityOrder
	 */
	void updateShopActivityOrder(ShopActivityOrder shopActivityOrder);

	/**
	 * 保存订单优惠表数据
	 * 
	 * @param shopActivityOrder
	 */
	void saveShopActivityOrder(ShopActivityOrder shopActivityOrder);

	/**
	 * 获取所有订单优惠表数据
	 * 
	 * @return
	 */
	List<ShopActivityOrder> findShopActivityOrderAllList();
	
}