package com.framework.loippi.entity;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Entity -
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_COMMON_MESSAGE")
public class ShopCommonMessage implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**  */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**  */
    @Column(name = "title")
    private String title;

    @Column(name = "subject")
    private String subject;

    @Column(name = "image")
    private String image;

    /**
     * 业务id
     */
    @Column(name = "biz_id")
    private Long bizId;

    /**
     * 消息类型 1-消息通知  2-提醒信息  3-订单信息 4-留言信息
     */
    @Column(name = "biz_type")
    private Integer bizType;

    /**  */
    @Column(name = "content")
    private String content;

    /**  */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 接收者
     */
    @Column(name = "send_uid")
    private String sendUid;

    /**
     * 1-系统消息  2-短信消息 3-通知栏消息
     */
    @Column(name = "type")
    private Integer type;

    /**
     * 用户类型 1-个人  2-分组  3-全部用户
     */
    @Column(name = "u_type")
    private Integer uType;

    private Integer TypeFiled;

    /**
     * 草稿 1-在线  2-草稿
     */
    @Column(name = "on_line")
    private Integer onLine;


    /**
     * 1-置顶  2-未置顶
     */
    @Column(name = "is_top")
    private Integer isTop;
    /**
     * 0-未回复  1-已回复
     */
    @Column(name = "is_reply")
    private Integer isReply;
    /**
     * 跳转路径 内容信息使用josn格式进行存储
     */
    @Column(name = "jump_path")
    private String jumpPath;
    /**
     * 跳转链接
     */
    @Column(name = "jump_url")
    private String jumpUrl;

    /**
     * 跳转路径回显信息
     */
    @Column(name = "echo_info")
    private String echoInfo;

    private String groupName;

    private String userName;
    private String mobile;

    /**
     *用于查询
     */
    private String searchStartTime;

    private String searchEndTime;
    //标题模糊查询
    private String titleLike;
}
