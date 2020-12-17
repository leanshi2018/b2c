package com.framework.loippi.result.user;

import com.framework.loippi.dto.UserMessageDto;
import com.framework.loippi.entity.user.RdMmBank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class MessageListResult {
    /** 消息id */
    private Long id;
    /** 消息标题 */
    private String title;
    /** 消息内容 */
    private String content;
    /** 创建时间 */
    private Date createTime;
    /**
     * 业务id（对于订单来讲为订单id）
     */
    private Long bizId;
    /**
     * 订单类型 1 零售订单 2 会员订单 3 pv订单 4 优惠订单 5 换购订单 6换货订单 7新会员启动包订单 8plus订单
     */
    private Integer orderType;

    public static List<MessageListResult> build(List<UserMessageDto> memberMessageList) {
        List<MessageListResult> messageListResultList=new ArrayList<>();
       if (memberMessageList!=null && memberMessageList.size()>0){
           for (UserMessageDto item:memberMessageList) {
               MessageListResult messageListResult=new MessageListResult();
               messageListResult.setId(item.getId());
               messageListResult.setContent(item.getContent());
               messageListResult.setCreateTime(item.getCreateTime());
               messageListResult.setTitle(item.getTitle());
               messageListResult.setBizId(Optional.ofNullable(item.getBizId()).orElse(0L));
               if(item.getOrderType()!=null){
                   messageListResult.setOrderType(item.getOrderType());
               }
               messageListResultList.add(messageListResult);
           }
       }
        return messageListResultList;
    }
}
