package com.framework.loippi.service.user;

import com.framework.loippi.entity.user.RdBonusMaster;
import com.framework.loippi.service.GenericService;
import com.framework.loippi.utils.Paramap;

/**
 * @author :ldq
 * @date:2019/8/15
 * @description:dubbo com.framework.loippi.service.user
 */
public interface RdBonusMasterService extends GenericService<RdBonusMaster, Long> {
	RdBonusMaster findByMCodeAndPeriodCode(Paramap put);
}
