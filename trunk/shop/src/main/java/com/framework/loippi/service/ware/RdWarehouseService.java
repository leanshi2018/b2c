package com.framework.loippi.service.ware;


import com.framework.loippi.entity.ware.RdWarehouse;
import com.framework.loippi.service.GenericService;

/**
 * SERVICE - RdWarehouse(仓库记录表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface RdWarehouseService  extends GenericService<RdWarehouse, Long> {

	RdWarehouse findByCode(String wareCode);

	RdWarehouse findByMmCode(String mmCode);
}
