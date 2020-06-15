package com.framework.loippi.service.ware;


import com.framework.loippi.entity.ware.RdInventoryWarning;
import com.framework.loippi.pojo.selfMention.GoodsType;
import com.framework.loippi.service.GenericService;

import java.util.List;

/**
 * SERVICE - RdInventoryWarning(仓库库存表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface RdInventoryWarningService  extends GenericService<RdInventoryWarning, Long> {

	void updateInventoryByWareCodeAndSpecId(String s, Long goodsSpecId,Integer quantity);

    List<GoodsType> findGoodsTypeByWareCode(String wareCode);
}
