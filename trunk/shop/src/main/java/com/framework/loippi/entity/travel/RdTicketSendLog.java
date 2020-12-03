package com.framework.loippi.entity.travel;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author zc
 * @date 2020/11/30
 * @implication 券发放日志记录
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_ticket_send_log")
public class RdTicketSendLog implements GenericEntity {
    private static final long serialVersionUID = 5081846432919091193L;
    /**
     * 主键id
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;
    /**
     * 券类型  1：优惠券  2：旅游券
     */
    @Column(name = "ticket_type" )
    private Integer ticketType;
    /**
     * 券id
     */
    @Column(name = "ticket_id" )
    private Long ticketId;
    /**
     * 券名称
     */
    @Column(name = "ticket_name" )
    private String ticketName;
    /**
     * 会员编号
     */
    @Column(name = "mm_code" )
    private String mmCode;
    /**
     * 会员昵称
     */
    @Column(name = "mm_nick_name" )
    private String mmNickName;
    /**
     * 发放数量
     */
    @Column(name = "num" )
    private Integer num;
    /**
     * 操作人编号
     */
    @Column(name = "operation_code" )
    private String operationCode;
    /**
     * 发放时间
     */
    @Column(name = "send_time" )
    private Date sendTime;
    /**
     * 备注
     */
    @Column(name = "remark" )
    private String remark;

    //*********************查询字段***************************
    /**
     * 查询时间左边限
     */
    private Date sendTimeLeft;
    /**
     * 查询时间右边限
     */
    private Date sendTimeRight;
}
