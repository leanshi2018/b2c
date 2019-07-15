package com.framework.loippi.entity.point;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Entity - 会员积分日志表
 *
 * @author zijing
 * @version 2.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "SHOP_POINTS_LOG")
public class ShopPointsLog implements GenericEntity {

    private static final long serialVersionUID = 5081846432919091193L;

    /**
     * 积分日志编号
     */
    @Column(id = true, name = "id", updatable = false)
    private Long id;

    /**
     * 会员编号
     */
    @Column(name = "pl_memberid")
    private Long plMemberid;

    /**
     * 会员名称
     */
    @Column(name = "pl_membername")
    private String plMembername;

    /**
     * 管理员编号
     */
    @Column(name = "pl_adminid")
    private Long plAdminid;

    /**
     * 管理员名称
     */
    @Column(name = "pl_adminname")
    private String plAdminname;

    /**
     * 积分数负数表示扣除
     */
    @Column(name = "pl_points")
    private BigDecimal plPoints;

    /**
     * 添加时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 操作描述
     */
    @Column(name = "pl_desc")
    private String plDesc;

    /**
     * 操作阶段
     */
    @Column(name = "pl_stage")
    private String plStage;

    /**
     * 创建者
     */
    @Column(name = "create_by")
    private Long createBy;

    /**
     * 创建时间
     */
    @Column(name = "create_date")
    private Date createDate;

    /**
     * 更新者
     */
    @Column(name = "update_by")
    private Long updateBy;

    /**
     * 更新时间
     */
    @Column(name = "update_date")
    private Date updateDate;

    /**
     * 备注信息
     */
    @Column(name = "remarks")
    private String remarks;

    /**
     * 删除标记（0：正常；1：删除）
     */
    @Column(name = "del_flag")
    private Integer delFlag;

    /**
     * 2-出  1-入
     */
    @Column(name = "pl_type")
    private Integer plType;

    /**
     * 是否升级
     */
    @Column(name = "if_bound")
    private Integer ifBound;

    /**
     * 会员名称
     */
    @Column(name = "gradename")
    private String gradename;
    
    
    /**
     * 积分类型(1消费积分 2打赏积分)
     */
    @Column(name = "biz_type")
    private Integer bizType;

}
