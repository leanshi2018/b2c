package com.framework.loippi.dao.user;

import com.framework.loippi.entity.user.RdBonusMaster;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.utils.Paramap;

/**
 * @author :ldq
 * @date:2019/8/15
 * @description:dubbo com.framework.loippi.dao.user
 */
public interface RdBonusMasterDao extends GenericDao<RdBonusMaster, Long> {
	RdBonusMaster findByMCodeAndPeriodCode(Paramap put);
}
