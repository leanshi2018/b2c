package com.framework.loippi.dao.user;

import java.util.Map;

import com.framework.loippi.entity.user.RdReceivableMaster;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * @author :ldq
 * @date:2020/5/13
 * @description:dubbo com.framework.loippi.dao.user
 */
public interface RdReceivableMasterDao extends GenericDao<RdReceivableMaster, String> {
	void updateNickNameByMCode(Map<String, Object> map);
}
