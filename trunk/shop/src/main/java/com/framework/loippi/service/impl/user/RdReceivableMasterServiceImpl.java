package com.framework.loippi.service.impl.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.dao.user.RdReceivableMasterDao;
import com.framework.loippi.entity.user.RdReceivableMaster;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdReceivableMasterService;

/**
 * @author :ldq
 * @date:2020/5/13
 * @description:dubbo com.framework.loippi.service.impl.user
 */
@Service
@Transactional
public class RdReceivableMasterServiceImpl extends GenericServiceImpl<RdReceivableMaster, String> implements RdReceivableMasterService {
	@Autowired
	private RdReceivableMasterDao rdReceivableMasterDao;

	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdReceivableMasterDao);
	}

	@Override
	public void updateNickNameByMCode(String nickName, String mmCode) {
		Map<String,Object> map = new HashMap<>();
		map.put("mmNickName",nickName);
		map.put("mmCode",mmCode);
		rdReceivableMasterDao.updateNickNameByMCode(map);
	}
}

