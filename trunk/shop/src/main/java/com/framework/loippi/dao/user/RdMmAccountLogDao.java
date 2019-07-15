package com.framework.loippi.dao.user;


import com.framework.loippi.entity.user.RdMmAccountLog;
import com.framework.loippi.entity.walet.ShopWalletLog;
import com.framework.loippi.mybatis.dao.GenericDao;

import java.util.List;

/**
 * DAO - RdMmAccountLog(会员账户交易日志表)
 * 
 * @author dzm
 * @version 2.0
 */
public interface RdMmAccountLogDao  extends GenericDao<RdMmAccountLog, Long> {

    void insertBatch(List<RdMmAccountLog> rdMmAccountLogList);

}
