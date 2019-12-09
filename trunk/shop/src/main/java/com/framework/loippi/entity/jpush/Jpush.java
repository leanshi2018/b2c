package com.framework.loippi.entity.jpush;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_jpush")
public class Jpush implements GenericEntity {
    private static final long serialVersionUID = 5081846432919091193L;
    /**
     * 主键id
     */
    @Column(name = "id")
    private Long id;
    /**
     * 推送平台  0：所有  1：android  2：ios正式 3：ios测试  4：安卓和ios测试 5：安卓和ios正式
     */
    @Column(name = "platform")
    private Integer platform;
    /**
     * 推送目标 全部用户填all，部分用户使用逗号拼接字符串
     */
    @Column(name = "audience")
    private String audience;
    /**
     * 通知标题
     */
    @Column(name = "notification")
    private String notification;
    /**
     * 通知内容
     */
    @Column(name = "message")
    private String message;
    /**
     * 推送时间
     */
    @Column(name = "push_time")
    private Date pushTime;
    /**
     * 跳转路径
     */
    @Column(name = "jump_path")
    private String jumpPath;
    /**
     * 跳转路径额外信息  json格式 eg; {“goodsId”:"1234567"}
     */
    @Column(name = "jump_json")
    private String jumpJson;
    /**
     * 跳转路径映射名称
     */
    @Column(name = "jump_name")
    private String jumpName;
    /**
     * 跳转链接  注：和跳转路径二者只能存在一个
     */
    @Column(name = "jump_link")
    private String jumpLink;
    /**
     * 推送状态 0：待发送  1：已发送 2：已停止
     */
    @Column(name = "status")
    private Integer status;
    /**
     * 推送方式  1：通知栏推送  2：其他
     */
    @Column(name = "push_method")
    private Integer pushMethod;
    //*********************查询字段******************************
    /**
     * 查询时间左
     */
    private String searchLeftTime;
    /**
     * 查询时间左
     */
    private String searchRightTime;
    /**
     * 查询内容，用于模糊查询内容
     */
    private String likeMessage;
}
