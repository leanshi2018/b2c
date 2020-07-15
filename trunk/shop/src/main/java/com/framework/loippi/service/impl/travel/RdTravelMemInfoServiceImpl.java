package com.framework.loippi.service.impl.travel;

import com.framework.loippi.dao.travel.RdTravelMemInfoDao;
import com.framework.loippi.entity.travel.RdTravelMemInfo;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.travel.RdTravelMemInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;

@Slf4j
@Service
public class RdTravelMemInfoServiceImpl extends GenericServiceImpl<RdTravelMemInfo, Long>
        implements RdTravelMemInfoService {
    @Resource
    private RdTravelMemInfoDao rdTravelMemInfoDao;
    @Override
    public void addList(ArrayList<RdTravelMemInfo> memInfos) {
        rdTravelMemInfoDao.addList(memInfos);
    }
}
