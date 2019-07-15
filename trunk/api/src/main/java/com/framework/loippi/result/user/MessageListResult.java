package com.framework.loippi.result.user;

import com.framework.loippi.dto.UserMessageDto;
import com.framework.loippi.entity.user.RdMmBank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 打赏积分
 * Created by Administrator on 2018/1/4.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageListResult {
    /** 消息id */
    private Long id;
    /** 消息标题 */
    private String title;
    /** 消息内容 */
    private String content;
    /** 创建时间 */
    private Date createTime;


    public static List<MessageListResult> build(List<UserMessageDto> memberMessageList) {
        List<MessageListResult> messageListResultList=new ArrayList<>();
       if (memberMessageList!=null && memberMessageList.size()>0){
           for (UserMessageDto item:memberMessageList) {
               MessageListResult messageListResult=new MessageListResult();
               messageListResult.setId(item.getId());
               messageListResult.setContent(item.getContent());
               messageListResult.setCreateTime(item.getCreateTime());
               messageListResult.setTitle(item.getTitle());
               messageListResultList.add(messageListResult);
           }
       }
        return messageListResultList;
    }
}
