package com.framework.loippi.dao.ware;

import com.framework.loippi.entity.ware.RdWareAllocation;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * 调拨单
 */
public interface RdWareAllocationDao extends GenericDao<RdWareAllocation, Long> {
	RdWareAllocation findBySn(String wareOrderSn);
}
