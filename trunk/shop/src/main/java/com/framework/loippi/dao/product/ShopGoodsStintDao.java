package com.framework.loippi.dao.product;

import java.util.List;

import com.framework.loippi.entity.product.ShopGoodsStint;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * @author :ldq
 * @date:2020/2/20
 * @description:dubbo com.framework.loippi.dao.product
 */
public interface ShopGoodsStintDao extends GenericDao<ShopGoodsStint, Long> {
	List<ShopGoodsStint> findBySerial(String specGoodsSerial);
}
