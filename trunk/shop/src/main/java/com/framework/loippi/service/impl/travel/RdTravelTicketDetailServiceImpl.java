package com.framework.loippi.service.impl.travel;

import com.framework.loippi.entity.travel.RdTicketSendLog;
import com.framework.loippi.entity.travel.RdTravelTicket;
import com.framework.loippi.entity.user.RdMmBasicInfo;
import com.framework.loippi.result.app.travel.TravelTicketDetailListResult;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.travel.RdTicketSendLogService;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;

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
    @Resource
    private TwiterIdService twiterIdService;
    @Resource
    private RdTicketSendLogService rdTicketSendLogService;

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

    /**
     * 发放旅游券
     * @param travelTicket 旅游券
     * @param num 数量
     * @param basicInfo 会员基础信息
     * @param remark 备注
     * @param username 操作人员信息
     */
    @Override
    public void sendTravelTicket(RdTravelTicket travelTicket, Integer num, RdMmBasicInfo basicInfo, String remark, String username) {
        //1.发放旅游券详情
        for (int i = 0; i <num; i++) {
            RdTravelTicketDetail rdTravelTicketDetail = new RdTravelTicketDetail();
            rdTravelTicketDetail.setId(twiterIdService.getTwiterId());
            rdTravelTicketDetail.setTravelId(travelTicket.getId());
            rdTravelTicketDetail.setTravelName(Optional.ofNullable(travelTicket.getTravelName()).orElse(""));
            rdTravelTicketDetail.setTicketPrice(Optional.ofNullable(travelTicket.getTicketPrice()).orElse(BigDecimal.ZERO));
            rdTravelTicketDetail.setTicketSn("T"+twiterIdService.getTwiterId());
            rdTravelTicketDetail.setStatus(0);
            rdTravelTicketDetail.setOwnCode(basicInfo.getMmCode());
            rdTravelTicketDetail.setOwnNickName(basicInfo.getMmNickName());
            rdTravelTicketDetail.setOwnTime(new Date());
            rdTravelTicketDetail.setImage(Optional.ofNullable(travelTicket.getImage()).orElse(""));
            rdTravelTicketDetailDao.insert(rdTravelTicketDetail);
        }
        //2.生成发放日志
        RdTicketSendLog log = new RdTicketSendLog();
        log.setId(twiterIdService.getTwiterId());
        log.setTicketType(2);
        log.setTicketId(travelTicket.getId());
        log.setTicketName(travelTicket.getTravelName());
        log.setMmCode(basicInfo.getMmCode());
        log.setMmNickName(basicInfo.getMmNickName());
        log.setNum(num);
        log.setOperationCode(username);
        log.setSendTime(new Date());
        if(remark!=null&&!"".equals(remark)){
            log.setRemark(remark);
        }
        rdTicketSendLogService.save(log);
    }
}
