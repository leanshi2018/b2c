package com.framework.loippi.service.impl.activity;

import com.framework.loippi.entity.activity.ActivityGuide;
import com.framework.loippi.service.activity.ActivityGuideService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ActivityGuideServiceImpl extends GenericServiceImpl<ActivityGuide, Long> implements ActivityGuideService {
}
