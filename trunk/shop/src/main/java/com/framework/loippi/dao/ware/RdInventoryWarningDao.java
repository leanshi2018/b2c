package com.framework.loippi.dao.ware;


import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.ware.RdInventoryWarning;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.pojo.selfMention.GoodsType;

/**
 * DAO - RdInventoryWarning(仓库库存表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface RdInventoryWarningDao  extends GenericDao<RdInventoryWarning, Long> {

    Long updateInventory(RdInventoryWarning rdInventoryWarning);

    void updateInventoryByWareCodeAndSpecId(Map<String, Object> map);

	List<RdInventoryWarning> findByWareCode(String wareCode);

	List<RdInventoryWarning> findByWareCodeAndOweInven(String wareCode);

	RdInventoryWarning findInventoryWarningByWareAndSpecId(Map<String, Object> map);

    List<GoodsType> findGoodsTypeByWareCode(String wareCode);

	RdInventoryWarning haveInventoryByWareCodeAndSpecId(Map<String, Object> map);

	void saveIn(RdInventoryWarning inventoryWarning);
}
