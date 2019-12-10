package com.framework.loippi.service.impl.jpush;

import com.framework.loippi.entity.jpush.Jpush;
import com.framework.loippi.service.impl.GenericServiceImpl;
import com.framework.loippi.service.jpush.JpushService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 极光推送业务层
 */
@Service
@Transactional
public class JpushServiceImpl extends GenericServiceImpl<Jpush, Long> implements JpushService {
}
