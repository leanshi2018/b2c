package com.framework.loippi.service.impl.ware;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.ware.RdWarehouseDao;
import com.framework.loippi.entity.ware.RdWarehouse;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.ware.RdWarehouseService;

import java.util.List;

/**
 * SERVICE - RdWarehouse(仓库记录表)
 * 
 * @author zijing
 * @version 2.0
 */
@Service
public class RdWarehouseServiceImpl extends GenericServiceImpl<RdWarehouse, Long> implements RdWarehouseService {
	
	@Autowired
	private RdWarehouseDao rdWarehouseDao;
	
	
	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdWarehouseDao);
	}

	@Override
	public RdWarehouse findByCode(String wareCode) {
		return rdWarehouseDao.findByCode(wareCode);
	}

	@Override
	public RdWarehouse findByMmCode(String mmCode) {
		return rdWarehouseDao.findByMmCode(mmCode);
	}

	@Override
	public List<RdWarehouse> findMentionWare() {
		return rdWarehouseDao.findMentionWare();
	}
	public List<RdWarehouse> findByMemberId(Long addId) {
		return rdWarehouseDao.findByMemberId(addId);
	}
}
