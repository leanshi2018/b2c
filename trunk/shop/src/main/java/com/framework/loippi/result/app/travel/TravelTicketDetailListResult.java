package com.framework.loippi.result.app.travel;

import com.framework.loippi.entity.coupon.Coupon;
import com.framework.loippi.entity.coupon.CouponDetail;
import com.framework.loippi.entity.travel.RdTravelTicket;
import com.framework.loippi.entity.travel.RdTravelTicketDetail;
import com.framework.loippi.result.app.coupon.CouponDetailListResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 个人旅游券列表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TravelTicketDetailListResult extends RdTravelTicketDetail implements Serializable {
    /**
     * 旅游券使用开始时间
     */
    private Date useStartTime;
    /**
     * 旅游券使用结束时间
     */
    private Date useEndTime;
    /**
     * 旅游券是否达到开始使用时间 true 开始  flase 未开始
     */
    private Boolean startFlag;
    /**
     * 旅游券使用规则
     */
    private String rules;

    public static ArrayList<TravelTicketDetailListResult> build(List<RdTravelTicketDetail> tickets, HashMap<Long, RdTravelTicket> map) {
        ArrayList<TravelTicketDetailListResult> results = new ArrayList<>();
        for (RdTravelTicketDetail ticket : tickets) {
            TravelTicketDetailListResult result = new TravelTicketDetailListResult();
            BeanUtils.copyProperties(ticket,result);
            result.setUseStartTime(map.get(ticket.getTravelId()).getUseStartTime());
            result.setUseEndTime(map.get(ticket.getTravelId()).getUseEndTime());
            result.setRules(map.get(ticket.getTravelId()).getRemark());
            Date useStartTime = map.get(ticket.getTravelId()).getUseStartTime();
            if(new Date().getTime()>=useStartTime.getTime()){
                result.setStartFlag(true);
            }else {
                result.setStartFlag(false);
            }
            results.add(result);
        }
        return results;
    }

    public static TravelTicketDetailListResult build2(RdTravelTicketDetail ticketDetail, RdTravelTicket rdTravelTicket) {
        TravelTicketDetailListResult result = new TravelTicketDetailListResult();
        BeanUtils.copyProperties(ticketDetail,result);
        result.setUseStartTime(rdTravelTicket.getUseStartTime());
        result.setUseEndTime(rdTravelTicket.getUseEndTime());
        result.setRules(rdTravelTicket.getRemark());
        Date useStartTime = rdTravelTicket.getUseStartTime();
        if(new Date().getTime()>=useStartTime.getTime()){
            result.setStartFlag(true);
        }else {
            result.setStartFlag(false);
        }
        return result;
    }
}
