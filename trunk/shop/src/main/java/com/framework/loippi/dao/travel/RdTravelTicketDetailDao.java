package com.framework.loippi.dao.travel;

import java.util.List;

import com.framework.loippi.entity.travel.RdTravelTicketDetail;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.result.travel.RdTravelTicketResult;

public interface RdTravelTicketDetailDao extends GenericDao<RdTravelTicketDetail, Long> {
	List<RdTravelTicketDetail> findNotUseTravelTicket();

    List<RdTravelTicketResult> findTypeAll(String mmCode);
}
