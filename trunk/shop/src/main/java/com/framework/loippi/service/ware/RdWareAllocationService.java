package com.framework.loippi.service.ware;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.framework.loippi.entity.ware.RdInventoryWarning;
import com.framework.loippi.entity.ware.RdWareAllocation;
import com.framework.loippi.entity.ware.RdWareOrder;
import com.framework.loippi.entity.ware.RdWarehouse;
import com.framework.loippi.result.selfMention.SelfOrderSubmitResult;
import com.framework.loippi.service.GenericService;

/**
 * @author :ldq
 * @date:2020/6/18
 * @description:dubbo com.framework.loippi.service.ware
 */
public interface RdWareAllocationService extends GenericService<RdWareAllocation, Long> {
	void addAllocation(RdWareOrder rdWareOrder, RdWareAllocation wareAllocation, Map<Long, Integer> specIdNumMap) throws Exception;

	void addAllocationOwe(RdWareOrder rdWareOrder, RdWareAllocation wareAllocation, List<RdInventoryWarning> inventoryWarningList) throws Exception;

	RdWareAllocation findBySn(String orderSn);

	List<RdWareAllocation> haveAllocation(String wareCode, int status);

	void addAllocationOweNew(RdWareOrder rdWareOrder, RdWareAllocation wareAllocation, JSONArray array) throws Exception;

	SelfOrderSubmitResult addAllocationOwe1(RdWarehouse warehouseIn, RdWarehouse warehouseOut, JSONArray array) throws Exception;

}
