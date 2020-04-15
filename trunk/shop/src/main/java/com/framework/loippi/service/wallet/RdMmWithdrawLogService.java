package com.framework.loippi.service.wallet;

import java.util.List;

import com.framework.loippi.entity.walet.RdMmWithdrawLog;
import com.framework.loippi.service.GenericService;

/**
 * @author :ldq
 * @date:2020/4/2
 * @description:dubbo com.framework.loippi.service.wallet
 */
public interface RdMmWithdrawLogService extends GenericService<RdMmWithdrawLog, Long> {
	void updateStatusBySnAndMCode(Integer withdrawStatus, String withdrawSn,String withdrawBank);

	RdMmWithdrawLog findBySn(String withdrawSn);

	List<RdMmWithdrawLog> findByMCode(String mCode);

	void updateStatusById(Integer withdrawStatus, Long id, String acct);
}
