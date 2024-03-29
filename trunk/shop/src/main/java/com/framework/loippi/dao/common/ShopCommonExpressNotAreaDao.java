package com.framework.loippi.dao.common;

import java.util.Map;

import com.framework.loippi.entity.common.ShopCommonExpressNotArea;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * @author :ldq
 * @date:2019/12/13
 * @description:dubbo com.framework.loippi.dao.common
 */
public interface ShopCommonExpressNotAreaDao extends GenericDao<ShopCommonExpressNotArea, Long> {
	ShopCommonExpressNotArea findByEIdAndAId(Map<String, Object> map);
}
