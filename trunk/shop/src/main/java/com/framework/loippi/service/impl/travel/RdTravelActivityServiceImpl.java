package com.framework.loippi.service.impl.travel;

import com.framework.loippi.dao.travel.RdTravelActivityDao;
import com.framework.loippi.entity.travel.RdTravelActivity;
import com.framework.loippi.entity.travel.RdTravelMemInfo;
import com.framework.loippi.entity.travel.RdTravelTicketDetail;
import com.framework.loippi.service.TwiterIdService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.travel.RdTravelActivityService;
import com.framework.loippi.service.travel.RdTravelMemInfoService;
import com.framework.loippi.service.travel.RdTravelTicketDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

    /**
     * 报名参团旅游活动 扣除旅游券 修改旅游活动信息 存储参团人信息
     * @param memInfos 参团人信息
     * @param useTicketNum 需要使用旅游券的数量
     * @param list 会员拥有旅游券集合
     * @param rdTravelActivity 旅游活动
     */
    @Override
    public void tuxedo(ArrayList<RdTravelMemInfo> memInfos, Integer useTicketNum, List<RdTravelTicketDetail> list, RdTravelActivity rdTravelActivity) throws Exception{
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
        if(Optional.ofNullable(rdTravelActivity.getNumTuxedo()).orElse(0)+memInfos.size()>=Optional.ofNullable(rdTravelActivity.getNumCeiling()).orElse(0)){
            rdTravelActivity.setStatus(2);
        }
        rdTravelActivityDao.update(rdTravelActivity);
    }
}
