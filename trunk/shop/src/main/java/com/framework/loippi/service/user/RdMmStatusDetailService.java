package com.framework.loippi.service.user;

import com.framework.loippi.entity.user.RdMmStatusDetail;
import com.framework.loippi.service.GenericService;

/**
 * @author :ldq
 * @date:2020/5/13
 * @description:dubbo com.framework.loippi.service.user
 */
public interface RdMmStatusDetailService extends GenericService<RdMmStatusDetail, Integer> {
	void updateNickNameByMCode(String nickName, String mmCode);
}
