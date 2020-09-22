package com.framework.loippi.service.travel;

import java.util.List;

import com.framework.loippi.entity.travel.RdTourismCompliance;
import com.framework.loippi.entity.travel.RdTravelTicket;
import com.framework.loippi.service.GenericService;

/**
 * @author :ldq
 * @date:2020/7/7
 * @description:dubbo com.framework.loippi.service.common
 */
public interface RdTourismComplianceService extends GenericService<RdTourismCompliance, Long> {
	RdTourismCompliance findByMmCode(String mmCode);

	void grantTicket(RdTravelTicket rdTravelTicket);

	List<RdTourismCompliance> findBySql();
}
