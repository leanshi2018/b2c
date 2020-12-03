package com.framework.loippi.service.travel;

import java.util.List;
import java.util.Map;

import com.framework.loippi.entity.travel.RdTravelTicket;
import com.framework.loippi.entity.travel.RdTravelTicketDetail;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.result.travel.RdTravelTicketResult;
import com.framework.loippi.service.GenericService;

public interface RdTravelTicketDetailService extends GenericService<RdTravelTicketDetail, Long> {
    void restoreOrDestroy(RdTravelTicketDetail ticketDetail, Integer species, Long id, String username, Long activityId) throws Exception;

    Map<String, List<RdTravelTicketDetail>> findNotUseTravelTicket();

    void sendTravelTicket(RdTravelTicket travelTicket, Integer num, RdMmBasicInfo basicInfo, String remark, String username);

    List<RdTravelTicketResult> findTypeAll(String mmCode);
}
