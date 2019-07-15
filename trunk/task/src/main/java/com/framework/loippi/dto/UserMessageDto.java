package com.framework.loippi.dto;

import com.framework.loippi.mybatis.ext.annotation.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Created by longbh on 2017/8/19.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMessageDto {

    /**  */
    private Long id;

    /**  */
    private String title;

    private String subject;

    private String image;

    /**
     * 业务id
     */
    private Long bizId;

    /**
     * 消息类型 1-消息通知  2-提醒信息  3-交易物流 4-留言信息
     */
    private Integer bizType;

    /**  */
    private String content;

    /**  */
    private Date createTime;

    /**
     * 接收者
     */
    private Long sendUid;

    /**
     * 1-系统消息  2-短信消息
     */
    private Integer type;

    /**
     * 用户类型 1-分组  2-全部用户
     */
    private Integer uType;

    /**
     * 草稿 1-在线  2-草稿
     */
    private Integer onLine;

    /**
     * 1-置顶  2-未置顶
     */
    private Integer isTop;

    private Integer paymentState;

    private Long mmId;

    private Integer isRead = 2;

    private String groupName;

    private String url = "";

}
