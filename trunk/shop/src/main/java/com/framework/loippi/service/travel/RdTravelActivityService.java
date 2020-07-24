package com.framework.loippi.service.travel;

import com.framework.loippi.entity.travel.RdTravelActivity;
import com.framework.loippi.entity.travel.RdTravelMemInfo;
import com.framework.loippi.entity.travel.RdTravelTicketDetail;
import com.framework.loippi.service.GenericService;

import java.util.ArrayList;
import java.util.List;

public interface RdTravelActivityService extends GenericService<RdTravelActivity, Long> {
    void tuxedo(ArrayList<RdTravelMemInfo> memInfos, Integer useTicketNum, List<RdTravelTicketDetail> list, RdTravelActivity rdTravelActivity, String mmCode, String nickname) throws Exception;
}
