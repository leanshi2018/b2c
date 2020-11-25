package com.framework.loippi.service.impl.travel;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.framework.loippi.dao.travel.RdTravelTicketDetailDao;
import com.framework.loippi.entity.travel.RdTravelTicketDetail;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.travel.RdTravelTicketDetailService;



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
    @Override
    public Map<String, List<RdTravelTicketDetail>> findNotUseTravelTicket() {

        List<RdTravelTicketDetail> detailList = rdTravelTicketDetailDao.findNotUseTravelTicket();
        Map<String, List<RdTravelTicketDetail>> excelMap = new HashMap<String, List<RdTravelTicketDetail>>();
        for (RdTravelTicketDetail ticketDetail : detailList) {

            List<RdTravelTicketDetail> orderExcelList = new ArrayList<RdTravelTicketDetail>();

            if (!excelMap.containsKey(ticketDetail.getOwnCode())){

                orderExcelList.add(ticketDetail);

            }else {
                orderExcelList = excelMap.get(ticketDetail.getOwnCode());
                /*int i = 0;
                for (RdTravelTicketDetail travelTicketExcel : orderExcelList) {
                    if (travelTicketExcel.getTicketPrice().setScale(0, RoundingMode.HALF_UP).intValue()!=ticketDetail.getTicketPrice().setScale(0, RoundingMode.HALF_UP).intValue()){//不相等
                        i=1;
                    }
                }
                if (i==1){
                    orderExcelList.add(ticketDetail);
                }*/
                orderExcelList.add(ticketDetail);
            }
            excelMap.put(ticketDetail.getOwnCode(),orderExcelList);
        }
        System.out.println("size"+excelMap.size());

        return excelMap;
    }
}
