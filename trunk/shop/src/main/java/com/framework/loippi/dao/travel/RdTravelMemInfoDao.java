package com.framework.loippi.dao.travel;

import com.framework.loippi.entity.travel.RdTravelMemInfo;
import com.framework.loippi.mybatis.dao.GenericDao;

import java.util.ArrayList;

public interface RdTravelMemInfoDao extends GenericDao<RdTravelMemInfo, Long> {
    void addList(ArrayList<RdTravelMemInfo> memInfos);
}
