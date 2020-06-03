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
 * 日会员信息统计记录
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_daily_mem_census")
public class DailyMemCensus implements GenericEntity {
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
     * 会员总数
     */
    @Column(name = "mem_total" )
    private Long memTotal;

    /**
     * 新增会员数
     */
    @Column(name = "daily_mem" )
    private Long dailyMem;

    /**
     * 新增新会员数
     */
    @Column(name = "daily_new_mem" )
    private Long dailyNewMem;

    /**
     * 新增老会员数
     */
    @Column(name = "daily_old_mem" )
    private Long dailyOldMem;

    /**
     * 新会员总数
     */
    @Column(name = "new_mem" )
    private Long newMem;

    /**
     * 老会员总数
     */
    @Column(name = "old_mem" )
    private Long oldMem;

    /**
     * 已消费普通会员数：按历史统计，当前级别为普通会员，但是有过消费记录的会员数量
     */
    @Column(name = "pay_common_mem" )
    private Long payCommonMem;

    /**
     * 未消费普通会员数：按历史统计，当前级别为普通会员，从未有过消费记录的会员数量
     */
    @Column(name = "no_pay_common_mem" )
    private Long noPayCommonMem;

    /**
     * 已消费普通会员客单价：按历史统计，当前级别为普通会员的总消费/(除以）已消费普通会员数
     */
    @Column(name = "pay_common_unit_price" )
    private BigDecimal payCommonUnitPrice;

    /**
     * 普通会员人数
     */
    @Column(name = "d0_num" )
    private Long d0Num;

    /**
     * vip人数
     */
    @Column(name = "d1_num" )
    private Long d1Num;

    /**
     * 代理会员人数
     */
    @Column(name = "d2_num" )
    private Long d2Num;

    /**
     * 初级代理店人数
     */
    @Column(name = "d3_num" )
    private Long d3Num;

    /**
     * 一级代理店人数
     */
    @Column(name = "d4_num" )
    private Long d4Num;

    /**
     * 二级代理店人数
     */
    @Column(name = "d5_num" )
    private Long d5Num;

    /**
     * 三级代理店人数
     */
    @Column(name = "d6_num" )
    private Long d6Num;

    /**
     * 旗舰店人数
     */
    @Column(name = "d7_num" )
    private Long d7Num;

    /**
     * 高级旗舰店人数
     */
    @Column(name = "d8_num" )
    private Long d8Num;

    /**
     * 超级旗舰店人数
     */
    @Column(name = "d9_num" )
    private Long d9Num;

    /**
     * 查询时间左边限
     */
    private String timeLeft;

    /**
     * 查询时间右边限
     */
    private String timeRight;
}
