package com.framework.loippi.service;

import com.framework.loippi.entity.GenTable;

import java.util.List;

/**
 * SERVICE - GenTable
 * 
 * @author Loippi Team
 * @version 1.0
 */
public interface GenTableService extends GenericService<GenTable, Long> {

    List<GenTable> findAllTable(String tableName);
}
