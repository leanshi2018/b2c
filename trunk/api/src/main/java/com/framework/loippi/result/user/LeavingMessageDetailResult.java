package com.framework.loippi.result.user;

import com.framework.loippi.dto.UserMessageDto;
import com.framework.loippi.entity.ShopCommonMessage;
import com.framework.loippi.entity.user.ShopMemberLeavingMessage;
import com.framework.loippi.mybatis.ext.annotation.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.mail.Flags;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 打赏积分
 * Created by Administrator on 2018/1/4.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeavingMessageDetailResult {
    /** 消息id */
    private Long id;
    /** 消息标题 */
    private String title;
    /** 创建时间 */
    private Date createTime;
    /** 消息图片 */
    private String image;
    /** 消息内容 */
    private String content;
    /** 是否可以回复 1 可以 2不可以 */
    private Integer status=2;
    /** 留言消息内容列表 */
    List<ReplyRecord> replyRecordList;
    @Data
   static  class ReplyRecord{
        /** 回复人员身份 */
        private String replyObject;
        /** 回复对象类型 0平台 1用户 */
        private Integer replyObjectType;
        /** 回复内容 */
        private String replyContent;
   }

    public static LeavingMessageDetailResult build(ShopCommonMessage shopCommonMessage,List<ShopMemberLeavingMessage> shopMemberLeavingMessageList) {
        LeavingMessageDetailResult leavingMessageDetailResult =new LeavingMessageDetailResult();
        List<ReplyRecord> replyRecordList=new ArrayList<>();
            if (shopCommonMessage!=null){
                leavingMessageDetailResult.setContent(shopCommonMessage.getContent());
                leavingMessageDetailResult.setCreateTime(shopCommonMessage.getCreateTime());
                leavingMessageDetailResult.setId(shopCommonMessage.getId());
                leavingMessageDetailResult.setImage(  Optional.ofNullable(shopCommonMessage.getImage()).orElse(""));
                leavingMessageDetailResult.setTitle(shopCommonMessage.getTitle());
            }
         if (shopMemberLeavingMessageList!=null && shopMemberLeavingMessageList.size()>0){
             for (int i = 0, flag=shopMemberLeavingMessageList.size(); i <flag ; i++) {
                 ReplyRecord replyRecord=new ReplyRecord();
                 ShopMemberLeavingMessage shopMemberLeavingMessage=shopMemberLeavingMessageList.get(i);
                 replyRecord.setReplyContent(shopMemberLeavingMessage.getReplyContent());
                 replyRecord.setReplyObject(shopMemberLeavingMessage.getReplyObject());
                 replyRecord.setReplyObjectType(shopMemberLeavingMessage.getReplyObjectType());
                 if (i==flag-1){
                     int index=shopMemberLeavingMessageList.get(i).getReplyObjectType();
                     if (index==0){  //最后是平台回复 可以进行回复
                         leavingMessageDetailResult.setStatus(1);
                     }else{
                         leavingMessageDetailResult.setStatus(2);
                     }
                 }
                 replyRecordList.add(replyRecord);
             }
         }
        leavingMessageDetailResult.setReplyRecordList(replyRecordList);
            return  leavingMessageDetailResult;
    }
}
