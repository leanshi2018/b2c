package com.framework.loippi.service.common;

import com.framework.loippi.entity.common.MemberShippingBehavior;
import com.framework.loippi.service.GenericService;

public interface MemberShippingBehaviorService extends GenericService<MemberShippingBehavior, Long> {
    void getMemberShippingBehavior();

    void getExcelByTime(String s, String s1);
}
