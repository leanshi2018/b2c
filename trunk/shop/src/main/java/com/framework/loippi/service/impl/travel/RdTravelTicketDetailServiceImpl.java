package com.framework.loippi.service.impl.travel;

import com.framework.loippi.dao.travel.RdTravelTicketDetailDao;
import com.framework.loippi.entity.travel.RdTravelTicketDetail;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.travel.RdTravelTicketDetailService;
import com.framework.loippi.utils.Paramap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class RdTravelTicketDetailServiceImpl extends GenericServiceImpl<RdTravelTicketDetail, Long>
        implements RdTravelTicketDetailService {
    @Resource
    private RdTravelTicketDetailDao rdTravelTicketDetailDao;

    @Override
    public void restoreOrDestroy(RdTravelTicketDetail ticketDetail, Integer species, Long id, String username, Long activityId) throws Exception{
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
            if(activityId!=null){
                ticketDetail.setUseActivityId(activityId);
            }
        }
        rdTravelTicketDetailDao.update(ticketDetail);
    }
}
