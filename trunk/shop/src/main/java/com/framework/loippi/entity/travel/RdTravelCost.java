package com.framework.loippi.entity.travel;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author zc
 * 旅游活动费用记录
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_travel_cost")
public class RdTravelCost implements GenericEntity {
    private static final long serialVersionUID = 5081846432919091193L;
    /**
     * 主键id
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 旅游活动id
     */
    @Column(name = "activity_id" )
    private Long activityId;

    /**
     * 旅游活动名称
     */
    @Column(name = "activity_name" )
    private String activityName;

    /**
     * 会员编号
     */
    @Column(name = "m_code" )
    private String mmCode;

    /**
     * 会员昵称
     */
    @Column(name = "m_nick_name" )
    private String mNickName;

    /**
     * 参加人数
     */
    @Column(name = "join_num" )
    private Integer joinNum;

    /**
     * 旅游券id
     */
    @Column(name = "ticket_id" )
    private Long ticketId;

    /**
     * 旅游券面值
     */
    @Column(name = "ticket_price" )
    private BigDecimal ticketPrice;

    /**
     * 使用旅游券张数
     */
    @Column(name = "use_num" )
    private Integer useNum;

    /**
     *旅游活动总费用，根据活动费用和参团人数确定
     */
    @Column(name = "money_total" )
    private BigDecimal moneyTotal;

    /**
     * 旅游券抵扣金额
     */
    @Column(name = "money_ticket" )
    private BigDecimal moneyTicket;

    /**
     * 需要补的差价
     */
    @Column(name = "moeny_fill" )
    private BigDecimal moenyFill;

    /**
     * 已经补的差价
     */
    @Column(name = "moeny_yet" )
    private BigDecimal moenyYet;

    /**
     * 剩余应补差价
     */
    @Column(name = "money_residue" )
    private BigDecimal moneyResidue;
}
