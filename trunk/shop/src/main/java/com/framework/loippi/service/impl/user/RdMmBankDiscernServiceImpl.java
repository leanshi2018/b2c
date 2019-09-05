package com.framework.loippi.service.impl.user;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.user.RdMmBankDiscernDao;
import com.framework.loippi.entity.user.RdMmBankDiscern;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdMmBankDiscernService;

/**
 * @author :ldq
 * @date:2019/9/4
 * @description:dubbo com.framework.loippi.service.impl.user
 */
@Service
public class RdMmBankDiscernServiceImpl extends GenericServiceImpl<RdMmBankDiscern, Long> implements RdMmBankDiscernService {

	@Autowired
	private RdMmBankDiscernDao rdMmBankDiscernDao;

	@Override
	public RdMmBankDiscern findByMCode(String mmCode) {
		return rdMmBankDiscernDao.findByMCode(mmCode);
	}

	@Override
	public void updateTimesByMCode(Integer numTimes, String mmCode) {
		Map<String,Object> map = new HashMap<>();
		map.put("mmCode",mmCode);
		map.put("numTimes",numTimes);
		rdMmBankDiscernDao.updateTimesByMCode(map);
	}

	@Override
	public void updateDateAndTimesByMCode(Date endDate, int numTimes, String mmCode) {
		Map<String,Object> map = new HashMap<>();
		map.put("mmCode",mmCode);
		map.put("numTimes",numTimes);
		map.put("discernDate",endDate);
		rdMmBankDiscernDao.updateDateAndTimesByMCode(map);
	}
}
