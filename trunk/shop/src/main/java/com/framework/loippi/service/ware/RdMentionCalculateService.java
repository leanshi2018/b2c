package com.framework.loippi.service.ware;

import com.framework.loippi.entity.ware.RdMentionCalculate;
import com.framework.loippi.service.GenericService;

public interface RdMentionCalculateService extends GenericService<RdMentionCalculate, Long> {
    void statistical();
}
