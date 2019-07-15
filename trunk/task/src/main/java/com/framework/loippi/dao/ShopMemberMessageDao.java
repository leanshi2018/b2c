package com.framework.loippi.dao;

import com.framework.loippi.dto.ShopMemberDto;
import com.framework.loippi.dto.UserMessageDto;
import com.framework.loippi.entity.ShopMemberMessage;
import com.framework.loippi.mybatis.dao.GenericDao;
import com.framework.loippi.mybatis.paginator.domain.PageBounds;
import com.framework.loippi.mybatis.paginator.domain.PageList;
import com.framework.loippi.utils.Paramap;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * DAO - ShopMemberMessage(用户消息)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopMemberMessageDao extends GenericDao<ShopMemberMessage, Long> {

    Long countMessage(@Param("uid") Long uid, @Param("bizType") Integer bizType);

    List<ShopMemberDto> findAllMember(@Param("pageNumber") Integer pageNumber);

    List<ShopMemberDto> findGroupMember(@Param("groupId") Long groupId, @Param("pageNumber") Integer pageNumber);

    void updateReadMessage(@Param("uid") Long uid, @Param("bizType") Integer bizType);

    PageList<UserMessageDto> findMessagePage(Object var1, PageBounds var2);

    void deleteMessage(@Param("uid") Long uid, @Param("type") Integer type);

    PageList<UserMessageDto> findMsgOrderPage(Object var1, PageBounds var2);

    List<UserMessageDto> findMessagePageByIsRead(Paramap paramap);

    void addTrainRecordBatch(List<ShopMemberMessage> shopMemberMessageList);
}
