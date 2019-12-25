package com.framework.loippi.dao.common;

import org.apache.ibatis.annotations.Param;

import com.framework.loippi.entity.common.ShopCommonExpress;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * DAO - ShopCommonExpress(快递公司)
 * 
 * @author zijing
 * @version 2.0
 */
public interface ShopCommonExpressDao  extends GenericDao<ShopCommonExpress, Long> {

    ShopCommonExpress findByOrderId(@Param("orderId") Long orderId);

	ShopCommonExpress findById(Long id);

	Integer macSort();

	ShopCommonExpress findBySort(int eSort);
}
