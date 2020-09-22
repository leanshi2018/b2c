package com.framework.loippi.dao.ware;

import java.util.List;

import com.framework.loippi.entity.ware.RdWareOrder;
import com.framework.loippi.mybatis.dao.GenericDao;

/**
 * @author :ldq
 * @date:2020/6/18
 * @description:dubbo com.framework.loippi.dao.ware
 */
public interface RdWareOrderDao extends GenericDao<RdWareOrder, Long> {
	RdWareOrder findBySn(String orderSn);

	List<RdWareOrder> findByPaySn(String paySn);

	Long updateByIdAndOrderStateAndLockState(RdWareOrder order);
}
