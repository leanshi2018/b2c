package com.framework.loippi.service;

import com.framework.loippi.dto.PushMessage;

/**
 * 消息推送队列
 * Created by longbh on 2017/8/19.
 */
public interface PushService {
    void sendMessage(String content,String[] sendUidSort);

    void updateReadMessage(Long id);

    void updateReadUMessage(Long uid, Integer bizType);
}
