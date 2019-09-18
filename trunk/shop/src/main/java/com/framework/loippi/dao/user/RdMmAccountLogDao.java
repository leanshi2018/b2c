package com.framework.loippi.dao.user;


import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * DAO - RdMmAccountLog(会员账户交易日志表)
 * 
 * @author dzm
 * @version 2.0
 */
public interface RdMmAccountLogDao  extends GenericDao<RdMmAccountLog, Long> {

    void insertBatch(List<RdMmAccountLog> rdMmAccountLogList);

	RdMmAccountLog findByTransNumber(Integer transNumber);

	int updateCancellWD(Map<String, Object> map);
}
