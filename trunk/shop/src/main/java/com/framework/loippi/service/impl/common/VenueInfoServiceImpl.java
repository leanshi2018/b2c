package com.framework.loippi.service.impl.common;

import com.framework.loippi.entity.common.VenueInfo;
import com.framework.loippi.service.common.VenueInfoService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class VenueInfoServiceImpl extends GenericServiceImpl<VenueInfo, Long> implements VenueInfoService {
}
