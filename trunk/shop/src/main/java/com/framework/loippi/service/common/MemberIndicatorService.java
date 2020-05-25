package com.framework.loippi.service.common;

import com.framework.loippi.entity.common.MemberIndicator;
import com.framework.loippi.service.GenericService;

/**
 * 会员指标
 */
public interface MemberIndicatorService extends GenericService<MemberIndicator, Long> {
    void getMemberIndicator();
}
