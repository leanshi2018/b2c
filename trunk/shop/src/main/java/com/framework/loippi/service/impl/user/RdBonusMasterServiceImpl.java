package com.framework.loippi.service.impl.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.dao.user.RdBonusMasterDao;
import com.framework.loippi.entity.user.RdBonusMaster;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdBonusMasterService;
import com.framework.loippi.utils.Paramap;

/**
 * @author :ldq
 * @date:2019/8/15
 * @description:dubbo com.framework.loippi.service.impl.user
 */
@Service
@Transactional
public class RdBonusMasterServiceImpl extends GenericServiceImpl<RdBonusMaster, Long> implements RdBonusMasterService {
	@Autowired
	private RdBonusMasterDao rdBonusMasterDao;

	@Override
	public RdBonusMaster findByMCodeAndPeriodCode(Paramap put) {
		return rdBonusMasterDao.findByMCodeAndPeriodCode(put);
	}
}
