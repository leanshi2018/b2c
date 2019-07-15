package com.framework.loippi.dao.ware;


import com.framework.loippi.entity.ware.RdInventoryWarning;
import com.framework.loippi.mybatis.dao.GenericDao;
import org.apache.poi.ss.formula.functions.T;

/**
 * DAO - RdInventoryWarning(仓库库存表)
 * 
 * @author zijing
 * @version 2.0
 */
public interface RdInventoryWarningDao  extends GenericDao<RdInventoryWarning, Long> {

    Long updateInventory(RdInventoryWarning rdInventoryWarning);

}
