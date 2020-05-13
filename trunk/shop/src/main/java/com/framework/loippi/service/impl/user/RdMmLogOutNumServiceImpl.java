package com.framework.loippi.service.impl.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.dao.user.RdMmLogOutNumDao;
import com.framework.loippi.entity.user.RdMmLogOutNum;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdMmLogOutNumService;

/**
 * @author :ldq
 * @date:2020/5/12
 * @description:dubbo com.framework.loippi.service.impl.user
 */
@Service
@Transactional
public class RdMmLogOutNumServiceImpl extends GenericServiceImpl<RdMmLogOutNum, Integer> implements RdMmLogOutNumService {

	@Autowired
	private RdMmLogOutNumDao rdMmLogOutNumDao;

	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdMmLogOutNumDao);
	}
}
