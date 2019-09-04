package com.framework.loippi.dao.user;

import java.util.Map;

import com.framework.loippi.entity.user.RdMmBankDiscern;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * @author :ldq
 * @date:2019/9/4
 * @description:dubbo com.framework.loippi.dao.user
 */
public interface RdMmBankDiscernDao extends GenericDao<RdMmBankDiscern, Long> {
	RdMmBankDiscern findByMCode(String mmCode);

	void updateTimesByMCode(Map<String, Object> map);

	void updateDateAndTimesByMCode(Map<String, Object> map);
}
