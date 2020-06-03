package com.framework.loippi.entity.common;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;

/**
 * 会员指标
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_mem_indicator")
public class MemberIndicator implements GenericEntity {
    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 主键自增
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 周期编号
     */
    @Column(name = "period_code")
    private String periodCode;
    /**
     * 统计时间
     */
    @Column(name = "statistical_time")
    private Date statisticalTime;
    /**
     * 总会员数：截止到当前所有的会员数（不含已注销）
     */
    @Column(name = "member_num_total")
    private Long memberNumTotal;
    /**
     * 启动数：当期有过登录行为的会员数
     */
    @Column(name = "start_num")
    private Long startNum;
    /**
     *  购买人数：当期有过购买行为
     */
    @Column(name = "buy_num")
    private Long buyNum;
    /**
     *  购买≥25MI：当期购买≥25MI
     */
    @Column(name = "buy_more_25")
    private Long buyMore25;
    /**
     *  购买≥50MI：当期购买≥50MI
     */
    @Column(name = "buy_more_50")
    private Long buyMore50;
    /**
     *  购买≥25MI：定义的活跃会员（当期购买≥25MI）人数/总会员人数
     */
    @Column(name = "active_more_25")
    private BigDecimal activeMore25;
    /**
     *  购买≥50MI：定义的活跃会员（当期购买≥50MI）人数/总会员人数
     */
    @Column(name = "active_more_50")
    private BigDecimal activeMore50;
    /**
     *  会员年度复购人数0次：未购买过有效订单的人数
     */
    @Column(name = "buy_zero")
    private Long buyZero;
    /**
     *  会员年度复购人数1次：购买过1次有效订单的人数
     */
    @Column(name = "buy_one")
    private Long buyOne;
    /**
     *  会员年度复购人数2次：购买过2次有效订单的人数
     */
    @Column(name = "buy_two")
    private Long buyTwo;
    /**
     *  一次复购率：人数/（除以）购买2次的人数
     */
    @Column(name = "re_purchase_one")
    private BigDecimal rePurchaseOne;
    /**
     *  >2次：购买超过2次有效订单的人数
     */
    @Column(name = "buy_more_two")
    private Long buyMoreTwo;
    /**
     *  二次复购率:人数/购买2次以上
     */
    @Column(name = "re_purchase_two")
    private BigDecimal rePurchaseTwo;
    /**
     *  回购人数：上一期购买过商品的用户，此月仍然有购买行为的用户数
     */
    @Column(name = "buy_back")
    private Long buyBack;
    /**
     *  上期有过购买行为的人数
     */
    @Column(name = "period_before_num")
    private Long periodBeforeNum;
    /**
     *  回购率：回购人数/（除以）上一期购买过的用户
     */
    @Column(name = "repurchase_rate")
    private BigDecimal repurchaseRate;
    /**
     *   用户购买次数 （按每期计算）1次 人数
     */
    @Column(name = "buy_one_period")
    private Long buyOnePeriod;
    /**
     *   用户购买次数 （按每期计算）2次 人数
     */
    @Column(name = "buy_two_period")
    private Long buyTwoPeriod;
    /**
     *   用户购买次数 （按每期计算）2次以上 人数
     */
    @Column(name = "buy_more_two_period")
    private Long buyMoreTwoPeriod;
    /**
     *   ≥3个月：距离上一次购物3个月没有购物的用户数
     */
    @Column(name = "month3_nobuy_num")
    private Long month3NobuyNum;
    /**
     *  3月未购买会员流失率
     */
    @Column(name = "month3_nobuy_rate")
    private BigDecimal month3NobuyRate;
    /**
     *   ≥6个月：距离上一次购物6个月没有购物的用户数
     */
    @Column(name = "month6_nobuy_num")
    private Long month6NobuyNum;
    /**
     *  6月未购买会员流失率
     */
    @Column(name = "month6_nobuy_rate")
    private BigDecimal month6NobuyRate;

    /**
     *  指标状态 1.正常 2已弃用
     */
    @Column(name = "status")
    private Integer status;

    /**
     *  修改会员指标状态时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 查询用周期集合
     */
    private ArrayList<String> periodList;
}
