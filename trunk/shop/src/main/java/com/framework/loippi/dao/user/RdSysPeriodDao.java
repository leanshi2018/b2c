package com.framework.loippi.dao.user;


import java.util.Date;

import com.framework.loippi.entity.user.RdSysPeriod;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * DAO - RdSysPeriod(业务周期)
 * 
 * @author dzm
 * @version 2.0
 */
public interface RdSysPeriodDao  extends GenericDao<RdSysPeriod, Long> {

    String getSysPeriodService(Date date);

	RdSysPeriod getPeriodService(Date date);

    RdSysPeriod findByPeriodCode(String periodCode);

    RdSysPeriod findLastPeriod();

}
