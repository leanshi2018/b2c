package com.framework.loippi.service.travel;

import com.framework.loippi.entity.travel.RdTravelCost;
import com.framework.loippi.service.GenericService;

public interface RdTravelCostService extends GenericService<RdTravelCost, Long> {
    void export(RdTravelCost costInfo);
}
