package com.framework.loippi.service.travel;

import com.framework.loippi.entity.travel.RdTravelMemInfo;
import com.framework.loippi.service.GenericService;

import java.util.ArrayList;

public interface RdTravelMemInfoService extends GenericService<RdTravelMemInfo, Long> {
    void addList(ArrayList<RdTravelMemInfo> memInfos);
}
