package com.framework.loippi.service.user;

import java.util.List;

import com.framework.loippi.entity.user.RdMmRemark;
import com.framework.loippi.service.GenericService;

/**
 * @author :ldq
 * @date:2020/12/3
 * @description:dubbo com.framework.loippi.service.user
 */
public interface RdMmRemarkService extends GenericService<RdMmRemark, Long> {
	List<RdMmRemark> findByMmCodeAndSpCode(String mmCode, String spCode);

	void deleteByMmCodeAndSpCode(String mmCode, String spCode);

	List<RdMmRemark> findByMmCode(String mmCode);
}
