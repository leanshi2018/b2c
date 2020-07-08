package com.framework.loippi.service.impl.travel;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.framework.loippi.dao.travel.RdTourismComplianceDao;
import com.framework.loippi.entity.travel.RdTourismCompliance;
import com.framework.loippi.service.travel.RdTourismComplianceService;
import com.framework.loippi.service.impl.GenericServiceImpl;

/**
 * @author :ldq
 * @date:2020/7/7
 * @description:dubbo com.framework.loippi.service.impl.common
 */
@Service
@Slf4j
public class RdTourismComplianceServiceImpl extends GenericServiceImpl<RdTourismCompliance, Long> implements RdTourismComplianceService {

	@Resource
	private RdTourismComplianceDao rdTourismComplianceDao;

	@Autowired
	public void setGenericDao() {
		super.setGenericDao(rdTourismComplianceDao);
	}
}
