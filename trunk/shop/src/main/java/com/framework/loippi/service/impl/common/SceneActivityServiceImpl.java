package com.framework.loippi.service.impl.common;

import com.framework.loippi.entity.common.SceneActivity;
import com.framework.loippi.service.common.SceneActivityService;
import com.framework.loippi.service.impl.GenericServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class SceneActivityServiceImpl extends GenericServiceImpl<SceneActivity, Long> implements SceneActivityService {
}
