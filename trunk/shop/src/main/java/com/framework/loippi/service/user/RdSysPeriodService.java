package com.framework.loippi.service.user;

import com.framework.loippi.entity.user.RdSysPeriod;
import com.framework.loippi.service.GenericService;

import java.util.Date;

/**
 * SERVICE - RdSysPeriod(业务周期)
 * 
 * @author dzm
 * @version 2.0
 */
public interface RdSysPeriodService  extends GenericService<RdSysPeriod, Long> {

    String getSysPeriodService(Date date);

}
