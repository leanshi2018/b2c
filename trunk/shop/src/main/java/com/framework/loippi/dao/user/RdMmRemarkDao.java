package com.framework.loippi.dao.user;

import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.user.RdMmRemark;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * @author :ldq
 * @date:2020/12/3
 * @description:dubbo com.framework.loippi.dao.user
 */
public interface RdMmRemarkDao extends GenericDao<RdMmRemark, Long> {
	List<RdMmRemark> findByMmCodeAndSpCode(Map<String, Object> map);

	void deleteByMmCodeAndSpCode(Map<String, Object> map);

	List<RdMmRemark> findByMmCode(String mmCode);
}
