package com.framework.loippi.entity.ware;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity - 自提店销售额及补贴记录
 *
 * @author zc
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_mention_calculate")
public class RdMentionCalculate implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;
    /**
     * 主键id
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 统计数据月份 yyyy-MM
     */
    @Column(name = "report_code" )
    private String reportCode;

    /**
     * 统计日期
     */
    @Column(name = "statistical_time" )
    private Date statisticalTime;

    /**
     * 自提店id
     */
    @Column(name = "mention_id" )
    private Integer mentionId;

    /**
     * 自提店名称
     */
    @Column(name = "mention_name" )
    private String mentionName;

    /**
     * 自提店店长会员编号
     */
    @Column(name = "m_code" )
    private String mCode;

    /**
     * 自提店店长昵称
     */
    @Column(name = "m_nick_name" )
    private String mNickName;

    /**
     * 当月订单数
     */
    @Column(name = "order_num" )
    private Integer orderNum;

    /**
     * 当月销售额
     */
    @Column(name = "income" )
    private BigDecimal income;

    /**
     * 补贴系数
     */
    @Column(name = "subsidies_coefficient" )
    private BigDecimal subsidiesCoefficient;

    /**
     * 补贴金额
     */
    @Column(name = "subsidies_acc" )
    private BigDecimal subsidiesAcc;

    /**
     * 状态 1:未发放补贴 2：已发放补贴 -1：已作废
     */
    @Column(name = "status" )
    private Integer status;

    /**
     * 发放人编号
     */
    @Column(name = "issue_code" )
    private String issueCode;

    /**
     * 发放时间
     */
    @Column(name = "issue_time" )
    private Date issueTime;
}
