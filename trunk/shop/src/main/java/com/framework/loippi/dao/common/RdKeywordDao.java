package com.framework.loippi.dao.common;

import java.util.List;

import com.framework.loippi.entity.common.RdKeyword;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * @author :ldq
 * @date:2019/12/31
 * @description:dubbo com.framework.loippi.dao.common
 */
public interface RdKeywordDao extends GenericDao<RdKeyword, Long> {
	List<RdKeyword> findByAll();
}
