package com.framework.loippi.service.impl.travel;

import com.framework.loippi.dao.travel.RdTravelActivityDao;
import com.framework.loippi.entity.travel.RdTravelActivity;
import com.framework.loippi.entity.travel.RdTravelCost;
import com.framework.loippi.entity.travel.RdTravelMemInfo;
import com.framework.loippi.entity.travel.RdTravelTicketDetail;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.travel.RdTravelActivityService;
import com.framework.loippi.service.travel.RdTravelCostService;
import com.framework.loippi.service.travel.RdTravelMemInfoService;
import com.framework.loippi.service.travel.RdTravelTicketDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class RdTravelActivityServiceImpl extends GenericServiceImpl<RdTravelActivity, Long>
        implements RdTravelActivityService {
    @Resource
    private RdTravelMemInfoService travelMemInfoService;
    @Resource
    private RdTravelActivityDao rdTravelActivityDao;
    @Resource
    private RdTravelTicketDetailService rdTravelTicketDetailService;
    @Resource
    private TwiterIdService twiterIdService;
    @Resource
    private RdTravelCostService rdTravelCostService;

    /**
     * 报名参团旅游活动 扣除旅游券 修改旅游活动信息 存储参团人信息
     * @param memInfos 参团人信息
     * @param useTicketNum 需要使用旅游券的数量
     * @param list 会员拥有旅游券集合
     * @param rdTravelActivity 旅游活动
     */
    @Override
    public void tuxedo(ArrayList<RdTravelMemInfo> memInfos, Integer useTicketNum, List<RdTravelTicketDetail> list, RdTravelActivity rdTravelActivity,String mmCode,String mNickName) throws Exception{
        //存储参团人信息
        travelMemInfoService.addList(memInfos);
        //扣除旅游券
        if(useTicketNum>0){
            for (int i = 0; i <useTicketNum ; i++) {
                RdTravelTicketDetail rdTravelTicketDetail = list.get(i);
                rdTravelTicketDetail.setStatus(1);
                rdTravelTicketDetail.setUseTime(new Date());
                rdTravelTicketDetail.setUseActivityId(rdTravelActivity.getId());
                rdTravelTicketDetail.setUseActivityCode(rdTravelActivity.getActivityCode());
                rdTravelTicketDetailService.update(rdTravelTicketDetail);
            }
        }
        //修改旅游活动信息
        rdTravelActivity.setNumTuxedo(Optional.ofNullable(rdTravelActivity.getNumTuxedo()).orElse(0) +memInfos.size());
        if(rdTravelActivity.getNumCeiling()!=null&&rdTravelActivity.getNumCeiling()!=0){
            if(Optional.ofNullable(rdTravelActivity.getNumTuxedo()).orElse(0)+memInfos.size()>=Optional.ofNullable(rdTravelActivity.getNumCeiling()).orElse(0)){
                rdTravelActivity.setStatus(2);
            }
        }
        rdTravelActivityDao.update(rdTravelActivity);
        //生成RdTravelCost记录
        RdTravelCost rdTravelCost = new RdTravelCost();
        rdTravelCost.setId(twiterIdService.getTwiterId());
        rdTravelCost.setActivityId(rdTravelActivity.getId());
        rdTravelCost.setActivityName(rdTravelActivity.getActivityName());
        rdTravelCost.setMmCode(mmCode);
        rdTravelCost.setMNickName(mNickName);
        rdTravelCost.setJoinNum(memInfos.size());
        BigDecimal moneyTotal = rdTravelActivity.getActivityCost().multiply(new BigDecimal(Integer.toString(memInfos.size()))).setScale(2,BigDecimal.ROUND_HALF_UP);
        rdTravelCost.setMoneyTotal(moneyTotal);
        if(useTicketNum>0){
            RdTravelTicketDetail ticketDetail = list.get(0);
            rdTravelCost.setTicketId(ticketDetail.getTravelId());
            rdTravelCost.setTicketPrice(ticketDetail.getTicketPrice());
            rdTravelCost.setUseNum(useTicketNum);
            BigDecimal moneyTicket = ticketDetail.getTicketPrice().multiply(new BigDecimal(Integer.toString(useTicketNum))).setScale(2,BigDecimal.ROUND_HALF_UP);
            rdTravelCost.setMoneyTicket(moneyTicket);

        }else {
            rdTravelCost.setUseNum(0);
            rdTravelCost.setMoneyTicket(BigDecimal.ZERO);
        }
        rdTravelCost.setMoenyFill(rdTravelCost.getMoneyTotal().subtract(rdTravelCost.getMoneyTicket()));
        rdTravelCost.setMoenyYet(BigDecimal.ZERO);
        rdTravelCost.setMoneyResidue(rdTravelCost.getMoenyFill().subtract(rdTravelCost.getMoenyYet()));
        rdTravelCostService.save(rdTravelCost);
    }
}
