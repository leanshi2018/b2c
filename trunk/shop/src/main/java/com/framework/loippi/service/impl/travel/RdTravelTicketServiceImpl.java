package com.framework.loippi.service.impl.travel;

import com.framework.loippi.dao.travel.RdTravelTicketDao;
import com.framework.loippi.entity.travel.RdTravelTicket;
import com.framework.loippi.entity.travel.RdTravelTicketDetail;
import com.framework.loippi.entity.user.ShopMemberPaymentTally;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.trade.ShopMemberPaymentTallyService;
import com.framework.loippi.service.travel.RdTravelTicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class RdTravelTicketServiceImpl extends GenericServiceImpl<RdTravelTicket, Long>
        implements RdTravelTicketService {

    @Resource
    private RdTravelTicketDao rdTravelTicketDao;
    @Resource
    private TwiterIdService twiterIdService;

    @Override
    public Map<String, String> saveOrEditCoupon(RdTravelTicket travelTicket, Long id, String username) {
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("code", "0");
        String errorMsg = "";
        resultMap.put("msg", errorMsg);
        if(travelTicket.getId()==null){
            Long twiterId = twiterIdService.getTwiterId();
            travelTicket.setId(twiterId);
            travelTicket.setCreateCode(Long.toString(id));
            travelTicket.setCreateName(username);
            travelTicket.setCreateTime(new Date());
            Long flag = rdTravelTicketDao.insert(travelTicket);
            if(flag==1){
                resultMap.put("code", "1");
            }
        }else {
            Long flag = rdTravelTicketDao.update(travelTicket);
            if(flag==1){
                resultMap.put("code", "1");
            }
        }
        return resultMap;
    }
}
