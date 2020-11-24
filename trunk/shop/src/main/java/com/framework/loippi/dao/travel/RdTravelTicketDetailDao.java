package com.framework.loippi.dao.travel;

import java.util.List;

import com.framework.loippi.entity.travel.RdTravelTicketDetail;
import com.framework.loippi.mybatis.dao.GenericDao;

public interface RdTravelTicketDetailDao extends GenericDao<RdTravelTicketDetail, Long> {
	List<RdTravelTicketDetail> findNotUseTravelTicket();
}
