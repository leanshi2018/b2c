package com.framework.loippi.dao.travel;

import com.framework.loippi.entity.travel.RdTourismCompliance;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * @author :ldq
 * @date:2020/7/7
 * @description:dubbo com.framework.loippi.dao.common
 */
public interface RdTourismComplianceDao extends GenericDao<RdTourismCompliance, Long> {
	RdTourismCompliance findByMmCode(String mmCode);
}
