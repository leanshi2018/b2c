package com.framework.loippi.service.impl.walet;

import org.springframework.beans.factory.annotation.Autowired;

import com.framework.loippi.dao.user.RdMmBasicInfoDao;
import com.framework.loippi.dao.walet.RdMmWithdrawLogDao;
import com.framework.loippi.entity.walet.RdMmWithdrawLog;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.wallet.RdMmWithdrawLogService;

/**
 * @author :ldq
 * @date:2020/4/2
 * @description:dubbo com.framework.loippi.service.impl.walet
 */
public class RdMmWithdrawLogServiceImpl extends GenericServiceImpl<RdMmWithdrawLog, Long> implements RdMmWithdrawLogService {

	@Autowired
	private RdMmWithdrawLogDao rdMmWithdrawLogDao;
	@Autowired
	private RdMmBasicInfoDao rdMmBasicInfoDao;

	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdMmWithdrawLogDao);
	}
}
