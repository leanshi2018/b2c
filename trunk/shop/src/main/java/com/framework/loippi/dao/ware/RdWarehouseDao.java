package com.framework.loippi.dao.ware;


import java.util.List;

import com.framework.loippi.entity.ware.RdWarehouse;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * DAO - RdWarehouse(仓库记录表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface RdWarehouseDao  extends GenericDao<RdWarehouse, Long> {

	RdWarehouse findByCode(String wareCode);

	RdWarehouse findByMmCode(String mmCode);

	List<RdWarehouse> findByMemberId(Long mentionId);
}
