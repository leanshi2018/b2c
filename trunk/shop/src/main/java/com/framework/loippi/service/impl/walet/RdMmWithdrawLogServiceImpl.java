package com.framework.loippi.service.impl.walet;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.walet.RdMmWithdrawLogDao;
import com.framework.loippi.entity.walet.RdMmWithdrawLog;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.wallet.RdMmWithdrawLogService;

/**
 * @author :ldq
 * @date:2020/4/2
 * @description:dubbo com.framework.loippi.service.impl.walet
 */
@Service
public class RdMmWithdrawLogServiceImpl extends GenericServiceImpl<RdMmWithdrawLog, Long> implements RdMmWithdrawLogService {

	@Autowired
	private RdMmWithdrawLogDao rdMmWithdrawLogDao;

	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdMmWithdrawLogDao);
	}

	@Override
	public void updateStatusBySnAndMCode(Integer withdrawStatus, String withdrawSn,String withdrawBank) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("withdrawStatus",withdrawStatus);
		map.put("withdrawSn",withdrawSn);
		map.put("withdrawBank",withdrawBank);
		rdMmWithdrawLogDao.updateStatusBySnAndMCode(map);
	}

	@Override
	public RdMmWithdrawLog findBySn(String withdrawSn) {
		return rdMmWithdrawLogDao.findBySn(withdrawSn);
	}

	@Override
	public RdMmWithdrawLog findByMCode(String mCode) {
		return rdMmWithdrawLogDao.findByMCode(mCode);
	}
}
