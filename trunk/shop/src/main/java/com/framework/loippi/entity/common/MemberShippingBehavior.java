package com.framework.loippi.entity.common;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 新老会员购买行为统计
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_mem_shipping_behavior")
public class MemberShippingBehavior implements GenericEntity {
    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 主键自增
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;
    /**
     * 日期 格式YYYY-MM-DD
     */
    @Column(name = "report_code" )
    private String reportCode;

    /**
     * 数据生成时间
     */
    @Column(name = "statistical_time" )
    private Date statisticalTime;

    /**
     * 0次：新会员升级到vip后，无购买行为的用户数量
     */
    @Column(name = "new_vip_buy_zero" )
    private Integer newVipBuyZero;

    /**
     * 升级vip后购买0次占升级到vip及其以上等级的新会员比例
     */
    @Column(name = "buy_zero_rate" )
    private BigDecimal buyZeroRate;
    /**
     * 1次：新会员升级到vip后，有过1次购买行为的用户数量
     */
    @Column(name = "new_vip_buy_one" )
    private Integer newVipBuyOne;

    /**
     * 升级后购买1次占升级到vip及其以上等级的新会员比例
     */
    @Column(name = "buy_one_rate" )
    private BigDecimal buyOneRate;
    /**
     * ≥2次：新会员升级到vip后，有过2次或以上购买行为的用户数量
     */
    @Column(name = "new_vip_buy_twomore" )
    private Integer newVipBuyTwomore;

    /**
     * 升级后购买2次及其以上占升级到vip及其以上等级的新会员比例
     */
    @Column(name = "buy_twomore_rate" )
    private BigDecimal buyTwomoreRate;
    /**
     * 0次：老会员注册后，无购买行为的用户数量
     */
    @Column(name = "old_mem_buy_zero" )
    private Integer oldMemBuyZero;

    /**
     * 老会员注册后购买0次订单占总的老会员绑定人数的比例
     */
    @Column(name = "old_buy_zero_rate" )
    private BigDecimal oldBuyZeroRate;
    /**
     * 1次：老会员注册后，有过1次购买行为的用户数量
     */
    @Column(name = "old_mem_buy_one" )
    private Integer oldMemBuyOne;

    /**
     * 老会员注册后购买1次订单占总的老会员绑定人数的比例
     */
    @Column(name = "old_buy_one_rate" )
    private BigDecimal oldBuyOneRate;
    /**
     * ≥2次：老会员注册后，有过2次或以上购买行为的用户数量
     */
    @Column(name = "old_mem_buy_twomore" )
    private Integer oldMemBuyTwomore;

    /**
     * 老会员注册后购买2次及其以上订单占总的老会员绑定人数的比例
     */
    @Column(name = "old_buy_twomore_rate" )
    private BigDecimal oldBuyTwomoreRate;

    /**
     * 查询时间左边限
     */
    private String timeLeft;

    /**
     * 查询时间右边限
     */
    private String timeRight;
}
