package com.framework.loippi.service.impl.walet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.dao.walet.RdBizPayDao;
import com.framework.loippi.entity.walet.RdBizPay;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.wallet.RdBizPayService;

/**
 * @author :ldq
 * @date:2020/4/9
 * @description:dubbo com.framework.loippi.service.impl.walet
 */
@Service
@Transactional
public class RdBizPayServiceImpl extends GenericServiceImpl<RdBizPay, Long> implements RdBizPayService {
	@Autowired
	private RdBizPayDao rdBizPayDao;

	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdBizPayDao);
}

	@Override
	public void updateStatus(String paySn) {
		rdBizPayDao.updateStatus(paySn);
	}

	@Override
	public List<RdBizPay> findByPaysn(String paySn) {
		return rdBizPayDao.findByPaysn(paySn);
	}

	@Override
	public List<RdBizPay> findByPaysnAndStatus(String paySn, Integer invalidStatus) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("paySn",paySn);
		map.put("invalidStatus",invalidStatus);
		return rdBizPayDao.findByPaysnAndStatus(map);
	}
}
