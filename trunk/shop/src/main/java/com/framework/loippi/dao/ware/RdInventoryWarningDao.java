package com.framework.loippi.dao.ware;


import java.util.Map;

import com.framework.loippi.entity.ware.RdInventoryWarning;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * DAO - RdInventoryWarning(仓库库存表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface RdInventoryWarningDao  extends GenericDao<RdInventoryWarning, Long> {

    Long updateInventory(RdInventoryWarning rdInventoryWarning);

    void updateInventoryByWareCodeAndSpecId(Map<String, Object> map);
}
