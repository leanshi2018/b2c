package com.framework.loippi.service.impl.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.dao.user.RdMmStatusDetailDao;
import com.framework.loippi.entity.user.RdMmStatusDetail;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdMmStatusDetailService;

/**
 * @author :ldq
 * @date:2020/5/13
 * @description:dubbo com.framework.loippi.service.impl.user
 */
@Service
@Transactional
public class RdMmStatusDetailServiceImpl extends GenericServiceImpl<RdMmStatusDetail, Integer> implements RdMmStatusDetailService {
	@Autowired
	private RdMmStatusDetailDao rdMmStatusDetailDao;

	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdMmStatusDetailDao);
	}

	@Override
	public void updateNickNameByMCode(String nickName, String mmCode) {
		Map<String,Object> map = new HashMap<>();
		map.put("mmNickName",nickName);
		map.put("mmCode",mmCode);
		rdMmStatusDetailDao.updateNickNameByMCode(map);
	}
}
