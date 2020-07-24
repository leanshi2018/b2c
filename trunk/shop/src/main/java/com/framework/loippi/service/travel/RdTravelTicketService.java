package com.framework.loippi.service.travel;

import com.framework.loippi.entity.travel.RdTravelTicket;
import com.framework.loippi.service.GenericService;

import java.util.Map;

public interface RdTravelTicketService extends GenericService<RdTravelTicket, Long> {
    Map<String, String> saveOrEditCoupon(RdTravelTicket travelTicket, Long id, String username);
}
