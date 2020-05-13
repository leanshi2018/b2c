package com.framework.loippi.service.user;


import java.util.HashMap;

import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.service.GenericService;

/**
 * SERVICE - RdMmAccountLog(会员账户交易日志表)
 * 
 * @author dzm
 * @version 2.0
 */
public interface RdMmAccountLogService  extends GenericService<RdMmAccountLog, Long> {

	RdMmAccountLog findByTransNumber(Integer transNumber);

	int updateCancellWD(Integer transNumber);

    RdMmAccountLog findCutByOrderId(HashMap<String, Object> map);

	void updateByCutOrderId(HashMap<String, Object> map);

	void updateNickNameByMCode(String nickName, String mmCode);
}
