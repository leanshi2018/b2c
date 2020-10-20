package com.framework.loippi.entity.user;

import com.framework.loippi.mybatis.eitity.GenericEntity;
import com.framework.loippi.mybatis.ext.annotation.Column;
import com.framework.loippi.mybatis.ext.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员关系修改日志表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rd_mm_relation_log")
public class MemberRelationLog implements GenericEntity {
    private static final long serialVersionUID = 5081846432919091193L;

    @Column(name = "id" )
    private long id;//主键id

    @Column(name = "rank_before" )
    private int rankBefore;//修改前等级

    @Column(name = "rank_after" )
    private int rankAfter;//修改后等级

    @Column(name = "retail_before" )
    private BigDecimal retailBefore;//日志前总零售额

    @Column(name = "retail_after" )
    private BigDecimal retailAfter;//日志后总零售额

    @Column(name = "pwd_before" )
    private String pwdBefore;//日志前登录密码

    @Column(name = "pwd_after" )
    private String pwdAfter;//日志后登录密码

    @Column(name = "mm_status_before" )
    private int mStatusBefore;//日志前会员状态

    @Column(name = "mm_status_after" )
    private int mStatusAfter;//日志后会员状态

    @Column(name = "spo_code_before" )
    private String spoCodeBefore;//日志前推荐人编号

    @Column(name = "spo_code_after" )
    private String spoCodeAfter;//日志后推荐人编号

    @Column(name = "ra_spo_status_before" )
    private int raSpoStatusBefore;//日志前旧系统转来推荐人状态

    @Column(name = "ra_spo_status_after" )
    private int raSpoStatusAfter;//日志后旧系统转来推荐人状态

    @Column(name = "pv_before" )
    private BigDecimal pvBefore;//日志前累计PV

    @Column(name = "pv_after" )
    private BigDecimal pvAfter;//日志后累计PV

    @Column(name = "new_old_flag_before" )
    private int newOldFlagBefore;//日志前会员类别

    @Column(name = "new_old_flag_after" )
    private int newOldFlagAfter;//日志后会员类别

    @Column(name = "category" )
    private int category;//日志类别 1:修改等级 2：修改累计购买额 3：修改密码 4：修改会员状态 5：修改推荐人 6：修改旧系统转来推荐人状态 7:修改累计pv值 8：修改会员类别 9：混合修改（一次修改多个字段）

    @Column(name = "create_time" )
    private Date createTime;//生成时间

    @Column(name = "mm_code" )
    private String mCode;//当前日志记录关联会员

    @Column(name = "remark" )
    private String remark;//日志备注
}
