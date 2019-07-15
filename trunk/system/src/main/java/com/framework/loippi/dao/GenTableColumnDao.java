package com.framework.loippi.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;

import com.framework.loippi.entity.GenTableColumn;
import com.framework.loippi.mybatis.dao.GenericDao;
/**
 * DAO - Gen Table
 * 
 * @author Loippi Team
 * @version 1.0
 */
public interface GenTableColumnDao  extends GenericDao<GenTableColumn, Long> {

	/**
	 * 获取列表
	 * @param tableId
	 * @return
	 */
	List<GenTableColumn> findByTableId(@Param("tableId") Long tableId);
	
	
	/**
	 * 根据Table编号删除所有列
	 * @param tableId
	 * @return
	 */
	Long deleteByTableId(@Param("tableId") Long tableId);
}
