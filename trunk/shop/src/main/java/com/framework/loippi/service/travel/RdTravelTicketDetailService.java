package com.framework.loippi.service.travel;

import com.framework.loippi.entity.travel.RdTravelTicketDetail;
import com.framework.loippi.service.GenericService;

public interface RdTravelTicketDetailService extends GenericService<RdTravelTicketDetail, Long> {
    void restoreOrDestroy(RdTravelTicketDetail ticketDetail, Integer species, Long id, String username) throws Exception;
}
