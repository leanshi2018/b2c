package com.framework.loippi.service.ware;


import java.util.List;

import com.framework.loippi.entity.ware.RdInventoryWarning;
import com.framework.loippi.pojo.selfMention.GoodsType;
import com.framework.loippi.service.GenericService;

/**
 * SERVICE - RdInventoryWarning(仓库库存表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface RdInventoryWarningService  extends GenericService<RdInventoryWarning, Long> {

	void updateInventoryByWareCodeAndSpecId(String s, Long goodsSpecId,Integer quantity);

	Integer findProductInventory(String ware, Long specId);

	List<RdInventoryWarning> findByWareCodeAndOweInven(String wareCode);

    List<GoodsType> findGoodsTypeByWareCode(String wareCode);

	List<RdInventoryWarning> findByWareCode(String wareCode);

	RdInventoryWarning findInventoryWarningByWareAndSpecId(String wareCode, Long specId);
}
