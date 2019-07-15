package com.framework.loippi.entity.common;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Entity - 系统反馈
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_COMMON_FEEDBACK")
public class ShopCommonFeedback implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * ID
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    @Column(name = "uid")
    private Long uid;

    /**
     * 标题
     */
    @Column(name = "title")
    private String title;

    /**
     * 内容
     */
    @Column(name = "content")
    private String content;

    /**
     * 邮箱
     */
    @Column(name = "email")
    private String email;

    /**
     * 电话
     */
    @Column(name = "phone")
    private String phone;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime = new Date();

    /**
     * IOS 1,ANDROID 2,other 3
     */
    @Column(name = "phone_type")
    private Integer phoneType = 3;

    /**
     * 1-已处理 2-未处理
     */
    @Column(name = "status")
    private Integer status = 2;

    /**
     * 操作用户
     */
    @Column(name = "manageid")
    private Long manageId;

    @Column(name = "op_time")
    private Date opTime;
    /**
     * 回复内容
     */
    @Column(name = "reply_content")
    private String replyContent;

    private String userName;
    private String managerName;


    /** 开始时间， 仅供页面查询 */
    private Date starttime;
    /** 结束时间， 仅供页面查询 */
    private Date endtime;

}
