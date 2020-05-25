package com.framework.loippi.service.impl.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.user.RdRanksNextMessageDao;
import com.framework.loippi.entity.user.RdRanksNextMessage;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdRanksNextMessageService;

/**
 * @author :ldq
 * @date:2020/5/21
 * @description:dubbo com.framework.loippi.service.impl.user
 */
@Service
public class RdRanksNextMessageServiceImpl extends GenericServiceImpl<RdRanksNextMessage, Long> implements RdRanksNextMessageService {
	@Autowired
	private RdRanksNextMessageDao rdRanksNextMessageDao;


	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdRanksNextMessageDao);
	}
}
