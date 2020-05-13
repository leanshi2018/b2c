package com.framework.loippi.service.user;

import com.framework.loippi.entity.user.RdReceivableMaster;
import com.framework.loippi.service.GenericService;

/**
 * @author :ldq
 * @date:2020/5/13
 * @description:dubbo com.framework.loippi.service.user
 */
public interface RdReceivableMasterService extends GenericService<RdReceivableMaster, String> {
	void updateNickNameByMCode(String nickName, String mmCode);
}
