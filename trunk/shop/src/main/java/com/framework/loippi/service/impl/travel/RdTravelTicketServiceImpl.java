package com.framework.loippi.service.impl.travel;

import com.framework.loippi.entity.travel.RdTravelTicket;
import com.framework.loippi.entity.user.ShopMemberPaymentTally;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.trade.ShopMemberPaymentTallyService;
import com.framework.loippi.service.travel.RdTravelTicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RdTravelTicketServiceImpl extends GenericServiceImpl<RdTravelTicket, Long>
        implements RdTravelTicketService {
}
