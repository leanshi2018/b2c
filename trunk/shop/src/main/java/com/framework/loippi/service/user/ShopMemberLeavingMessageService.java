package com.framework.loippi.service.user;


import com.framework.loippi.entity.user.ShopMemberLeavingMessage;
import com.framework.loippi.service.GenericService;

/**
 * SERVICE - ShopMemberLeavingMessage(留言消息记录表)
 * 
 * @author dzm
 * @version 2.0
 */
public interface ShopMemberLeavingMessageService extends GenericService<ShopMemberLeavingMessage, Long> {

    /**
     * 新增留言消息记录
     * @param replyContent 回复内容
     * @param replyObjectType 回复对象类型
     * @param bizId  关联id
     * @param memberId 用户id
     */
    void saveLeavingMessage(String replyContent, Integer replyObjectType,Long bizId,Long memberId);
}
