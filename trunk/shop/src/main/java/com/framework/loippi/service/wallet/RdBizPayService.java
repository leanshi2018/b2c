package com.framework.loippi.service.wallet;

import java.util.List;

import com.framework.loippi.entity.walet.RdBizPay;
import com.framework.loippi.service.GenericService;

/**
 * @author :ldq
 * @date:2020/4/9
 * @description:dubbo com.framework.loippi.service.wallet
 */
public interface RdBizPayService extends GenericService<RdBizPay, Long> {
	void updateStatus(String mmPaySn);

	List<RdBizPay> findByPaysn(String mmPaySn);

	List<RdBizPay> findByPaysnAndStatus(String paySn, Integer invalidStatus);

	void updateStatusByBizPaySn(String bizOrderNo);
}
