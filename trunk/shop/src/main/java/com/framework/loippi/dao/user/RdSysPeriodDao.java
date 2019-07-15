package com.framework.loippi.dao.user;


import com.framework.loippi.entity.user.RdSysPeriod;
import com.framework.loippi.mybatis.dao.GenericDao;

import java.util.Date;

/**
 * DAO - RdSysPeriod(业务周期)
 * 
 * @author dzm
 * @version 2.0
 */
public interface RdSysPeriodDao  extends GenericDao<RdSysPeriod, Long> {

    String getSysPeriodService(Date date);
}
