package com.framework.loippi.entity;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Entity - 用户消息
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_MEMBER_MESSAGE")
public class ShopMemberMessage implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**  */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**  */
    @Column(name = "msg_id")
    private Long msgId;

    /**
     * 消息类型 1-消息通知  2-提醒信息  3-订单信息 4-留言信息
     */
    @Column(name = "biz_type")
    private Integer bizType;

    /**
     * 1-已读  2-未读
     */
    @Column(name = "is_read")
    private Integer isRead;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    @Column(name = "uid")
    private Long uid;
}
