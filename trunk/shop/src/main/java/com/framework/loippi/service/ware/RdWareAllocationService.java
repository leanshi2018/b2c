package com.framework.loippi.service.ware;

import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.ware.RdInventoryWarning;
import com.framework.loippi.entity.ware.RdWareAllocation;
import com.framework.loippi.entity.ware.RdWareOrder;
import com.framework.loippi.service.GenericService;

/**
 * @author :ldq
 * @date:2020/6/18
 * @description:dubbo com.framework.loippi.service.ware
 */
public interface RdWareAllocationService extends GenericService<RdWareAllocation, Long> {
	void addAllocation(RdWareOrder rdWareOrder, RdWareAllocation wareAllocation, Map<Long, Integer> specIdNumMap) throws Exception;

	void addAllocationOwe(RdWareOrder rdWareOrder, RdWareAllocation wareAllocation, List<RdInventoryWarning> inventoryWarningList) throws Exception;
}
