package com.framework.loippi.service.wallet;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.framework.loippi.entity.walet.RdMmWithdrawLog;
import com.framework.loippi.service.GenericService;

/**
 * @author :ldq
 * @date:2020/4/2
 * @description:dubbo com.framework.loippi.service.wallet
 */
@Service
@Transactional
public interface RdMmWithdrawLogService extends GenericService<RdMmWithdrawLog, Long> {
}
