package com.framework.loippi.service;

import com.framework.loippi.dto.UserMessageDto;
import com.framework.loippi.entity.ShopMemberMessage;
import com.framework.loippi.support.Page;
import com.framework.loippi.support.Pageable;
import com.framework.loippi.utils.Paramap;

import java.util.List;

/**
 * SERVICE - ShopMemberMessage(用户消息)
 *
 * @author zijing
 * @version 2.0
 */
public interface ShopMemberMessageService extends GenericService <ShopMemberMessage, Long> {

    Long countMessage(Long uid, Integer bizType);

    Page <UserMessageDto> findMessagePage(Pageable pageable);

    Page <UserMessageDto> findMsgOrderPage(Pageable pageable);

    UserMessageDto findLast(Long uid, Integer bizType);

     UserMessageDto findLastByTypes(Long uid, List<Integer> bizTypeList);

    void deleteMessage(Long uid, Integer type);

    /**
     * 推送给用户交易物流站内信
     *
     * @param type 1推送发货信息  0推送签收信息
     */
    void saveLogisticMsg(Integer type, String title, String subTitle, String img, Long orderId, Long memberId);

    /**
     * 根据是否已读查询消息
     * @param paramap
     * @return
     */
    public List<UserMessageDto> findMessagePageByIsRead(Paramap paramap) ;

    /**
     * 新增留言消息
     * @param title
     * @param content
     * @param img
     * @param memberId
     */
    void saveLeavingMessage(String title, String content, String img,Long memberId);

    void addTrainRecordBatch(List<ShopMemberMessage> shopMemberMessageList);

    Integer findMessageRemindNum(long parseLong);

    Integer findMessageOrderNum(long parseLong);

    Integer findMessageLeaveNum(long parseLong);
}
