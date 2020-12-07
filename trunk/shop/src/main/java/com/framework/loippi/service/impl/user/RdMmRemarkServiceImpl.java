package com.framework.loippi.service.impl.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.dao.user.RdMmRemarkDao;
import com.framework.loippi.entity.user.RdMmRemark;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.user.RdMmRemarkService;

/**
 * @author :ldq
 * @date:2020/12/3
 * @description:dubbo com.framework.loippi.service.impl.user
 */
@Service
@Transactional
public class RdMmRemarkServiceImpl extends GenericServiceImpl<RdMmRemark, Long> implements RdMmRemarkService {
	@Resource
	private RdMmRemarkDao rdMmRemarkDao;

	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdMmRemarkDao);
	}

	@Override
	public List<RdMmRemark> findByMmCodeAndSpCode(String mmCode, String spCode) {
		Map<String,Object> map = new HashMap<>();
		map.put("mmCode",mmCode);
		map.put("spCode",spCode);
		return rdMmRemarkDao.findByMmCodeAndSpCode(map);
	}

	@Override
	public void deleteByMmCodeAndSpCode(String mmCode, String spCode) {
		Map<String,Object> map = new HashMap<>();
		map.put("mmCode",mmCode);
		map.put("spCode",spCode);
		rdMmRemarkDao.deleteByMmCodeAndSpCode(map);
	}

	@Override
	public List<RdMmRemark> findByMmCode(String mmCode) {
		return rdMmRemarkDao.findByMmCode(mmCode);
	}
}
