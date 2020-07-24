package com.framework.loippi.service.impl.travel;

import com.framework.loippi.dao.travel.RdTravelTicketDetailDao;
import com.framework.loippi.entity.travel.RdTravelTicketDetail;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.travel.RdTravelTicketDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Service
public class RdTravelTicketDetailServiceImpl extends GenericServiceImpl<RdTravelTicketDetail, Long>
        implements RdTravelTicketDetailService {
    @Resource
    private RdTravelTicketDetailDao rdTravelTicketDetailDao;

    @Override
    public void restoreOrDestroy(RdTravelTicketDetail ticketDetail, Integer species, Long id, String username) throws Exception{
        if(species==1){//恢复
            ticketDetail.setStatus(0);
            ticketDetail.setUseTime(null);
            ticketDetail.setUseActivityCode(null);
            ticketDetail.setUseActivityId(null);
        }
        if(species==2){//核销
            ticketDetail.setStatus(2);
            ticketDetail.setConfirmTime(new Date());
            ticketDetail.setConfirmCode(Long.toString(id));
        }
        rdTravelTicketDetailDao.update(ticketDetail);
    }

}
