package com.framework.loippi.result.user;

import com.framework.loippi.dto.UserMessageDto;
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
public class LeavingMessageListResult {
    /** 消息id */
    private Long id;
    /** 消息标题 */
    private String title;

    /** 创建时间 */
    private Date createTime;


    public static List<LeavingMessageListResult> build(List<UserMessageDto> memberMessageList) {
        List<LeavingMessageListResult> messageListResultList=new ArrayList<>();
       if (memberMessageList!=null && memberMessageList.size()>0){
           for (UserMessageDto item:memberMessageList) {
               LeavingMessageListResult messageListResult=new LeavingMessageListResult();
               messageListResult.setId(item.getId());
               messageListResult.setCreateTime(item.getCreateTime());
               messageListResult.setTitle(item.getTitle());
               messageListResultList.add(messageListResult);
           }
       }
        return messageListResultList;
    }
}
