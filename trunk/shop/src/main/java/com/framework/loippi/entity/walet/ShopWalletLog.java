package com.framework.loippi.entity.walet;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Entity - 预存款变更日志表
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_WALLET_LOG")
public class ShopWalletLog implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 自增编号
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 会员编号
     */
    @Column(name = "lg_member_id")
    private Long lgMemberId;

    /**
     * 会员名称
     */
    @Column(name = "lg_member_name")
    private String lgMemberName;

    /**
     * 管理员名称
     */
    @Column(name = "lg_admin_name")
    private String lgAdminName;

    /**
     * order_pay下单支付预存款,
     * order_freeze_balance下单冻结余额预存款,
     * order_freeze_hb下单冻结红包预存款,
     * order_cancel取消订单解冻预存款,
     * order_comb_pay下单支付被冻结的预存款,
     ** ocash_apply提现,
     */
    @Column(name = "lg_type")
    private String lgType;

    /**
     * 可用金额变更0表示未变更
     */
    @Column(name = "lg_av_amount")
    private BigDecimal lgAvAmount;

    /**
     * 冻结金额变更0表示未变更
     */
    @Column(name = "lg_freeze_amount")
    private BigDecimal lgFreezeAmount;

    /**
     * 存入
     */
    @Column(name = "lg_add_amount")
    private BigDecimal lgAddAmount;

    /**
     * 支出
     */
    @Column(name = "lg_rde_amount")
    private BigDecimal lgRdeAmount;

    /**
     * 添加时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 描述
     */
    @Column(name = "lg_desc")
    private String lgDesc;

    /**
     * 关联订单号（用于财务管理支出模块）
     */
    private String orderSn;
    /**
     * 业务id
     */
    @Column(name = "biz_id")
    private Long bizId;

    /**
     * 编号
     */
    @Column(name = "lg_sn")
    private String lgSn;

    @Column(name = "biz_sn")
    private String bizSn;

    /**
     * 店铺id
     */
    @Column(name = "store_id")
    private Long storeId;

    /**
     * 0:收入   1:支出
     */
    private Integer is_lg_add_rde;
    private Integer is_moneyPacket_redPacket;
    private List<String> lgTypes;
    private List<String> lgTypesParam;
    private Date searchStartTime;
    private Date searchEndTime;

}
