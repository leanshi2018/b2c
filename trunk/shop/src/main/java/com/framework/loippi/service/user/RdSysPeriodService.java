package com.framework.loippi.service.user;

import java.util.Date;

import com.framework.loippi.entity.user.RdSysPeriod;
import com.framework.loippi.service.GenericService;

/**
 * SERVICE - RdSysPeriod(业务周期)
 * 
 * @author dzm
 * @version 2.0
 */
public interface RdSysPeriodService  extends GenericService<RdSysPeriod, Long> {

    String getSysPeriodService(Date date);

    RdSysPeriod getPeriodService(Date date);

    RdSysPeriod findLastPeriod();

    RdSysPeriod findByPeriodCode(String periodCode);
}
