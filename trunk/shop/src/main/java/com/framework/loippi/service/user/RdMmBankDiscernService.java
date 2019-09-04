package com.framework.loippi.service.user;

import java.util.Date;

import com.framework.loippi.entity.user.RdMmBankDiscern;
import com.framework.loippi.service.GenericService;

/**
 * @author :ldq
 * @date:2019/9/4
 * @description:dubbo com.framework.loippi.service.user
 */
public interface RdMmBankDiscernService  extends GenericService<RdMmBankDiscern, Long> {
	RdMmBankDiscern findByMCode(String mmCode);

	void updateTimesByMCode(Integer numTimes, String mmCode);

	void updateDateAndTimesByMCode(Date endDate, int numTimes, String mmCode);
}
