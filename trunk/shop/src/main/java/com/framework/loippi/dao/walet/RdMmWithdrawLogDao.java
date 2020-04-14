package com.framework.loippi.dao.walet;

import java.util.Map;

import com.framework.loippi.entity.walet.RdMmWithdrawLog;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * @author :ldq
 * @date:2020/4/2
 * @description:dubbo com.framework.loippi.dao.walet
 */
public interface RdMmWithdrawLogDao extends GenericDao<RdMmWithdrawLog, Long> {
	void updateStatusBySnAndMCode(Map<String, Object> map);

	RdMmWithdrawLog findBySn(String withdrawSn);

	RdMmWithdrawLog findByMCode(String mCode);
}
