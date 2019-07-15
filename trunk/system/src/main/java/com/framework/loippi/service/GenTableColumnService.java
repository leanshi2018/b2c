package com.framework.loippi.service;

import com.framework.loippi.entity.GenTableColumn;

/**
 * SERVICE - GenTable
 * 
 * @author Loippi Team
 * @version 1.0
 */
public interface GenTableColumnService extends GenericService<GenTableColumn, Long> {
	
	/**
	 * 根据Table编号删除所有列
	 * @param tableId
	 * @return
	 */
	Long deleteByTableId(Long tableId);
}
